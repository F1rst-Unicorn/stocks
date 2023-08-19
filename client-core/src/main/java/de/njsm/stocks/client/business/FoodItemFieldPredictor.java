/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodForItemCreation;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.Location;
import io.reactivex.rxjava3.core.Maybe;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

import static de.njsm.stocks.client.business.entities.IdImpl.create;

class FoodItemFieldPredictor {

    private final FoodItemAddRepository repository;

    private final Localiser localiser;

    private final Clock clock;

    @Inject
    FoodItemFieldPredictor(FoodItemAddRepository repository, Localiser localiser, Clock clock) {
        this.repository = repository;
        this.localiser = localiser;
        this.clock = clock;
    }

    Prediction predictFor(IdImpl<Food> food) {
        return new Prediction(food);
    }

    Prediction predictFor(Maybe<FoodForItemCreation> food) {
        return new Prediction(food);
    }

    final class Prediction {

        private final Maybe<FoodForItemCreation> foodObservable;

        private Prediction(Maybe<FoodForItemCreation> foodObservable) {
            this.foodObservable = foodObservable;
        }

        private Prediction(IdImpl<Food> food) {
            this.foodObservable = repository.getFood(food);
        }

        LocalDate predictEatBy() {
            return predictEatByDateAsync().blockingGet();
        }

        Maybe<LocalDate> predictEatByDateAsync() {
            Maybe<Instant> instantVersion = foodObservable.flatMap(food1 -> {
                if (food1.expirationOffset().equals(Period.ZERO)) {
                    return Maybe.concat(repository.getMaxEatByOfPresentItemsOf(food1),
                                    repository.getMaxEatByEverOf(food1),
                                    Maybe.fromCallable(clock::get))
                            .firstElement();
                } else {
                    return Maybe.just(clock.get().plus(food1.expirationOffset()));
                }
            });
            return instantVersion.map(localiser::toLocalDate);
        }

        IdImpl<Location> predictLocation() {
            return predictLocationAsync().blockingGet();
        }

        Maybe<IdImpl<Location>> predictLocationAsync() {
            return foodObservable.flatMap(food1 -> food1.location().map(Maybe::just)
                    .orElseGet(() -> repository.getLocationWithMostItemsOfType(food1)
                            .switchIfEmpty(repository.getLocationMostItemsHaveBeenAddedTo(food1))
                            .switchIfEmpty(repository.getAnyLocation())
                            .switchIfEmpty(Maybe.just(create(-1)))));
        }
    }
}
