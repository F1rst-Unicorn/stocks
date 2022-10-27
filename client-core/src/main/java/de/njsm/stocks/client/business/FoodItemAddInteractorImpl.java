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

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.execution.Scheduler;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Instant;
import java.time.Period;
import java.util.List;

class FoodItemAddInteractorImpl implements FoodItemAddInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(FoodItemAddInteractorImpl.class);

    private final FoodItemAddService service;

    private final FoodItemAddRepository repository;

    private final Scheduler scheduler;

    private final Synchroniser synchroniser;

    private final ErrorRecorder errorRecorder;

    private final Localiser localiser;

    private final Clock clock;

    @Inject
    FoodItemAddInteractorImpl(FoodItemAddService service, FoodItemAddRepository repository, Scheduler scheduler, Synchroniser synchroniser, ErrorRecorder errorRecorder, Localiser localiser, Clock clock) {
        this.service = service;
        this.repository = repository;
        this.scheduler = scheduler;
        this.synchroniser = synchroniser;
        this.errorRecorder = errorRecorder;
        this.localiser = localiser;
        this.clock = clock;
    }

    @Override
    public Observable<FoodItemAddData> getFormData(Id<Food> id) {
        Observable<FoodForItemCreation> foodObservable = repository.getFood(id);
        Observable<Instant> predictedEatBy = foodObservable.flatMapMaybe(food -> {
            if (food.expirationOffset().equals(Period.ZERO)) {
                return Maybe.concat(repository.getMaxEatByOfPresentItemsOf(food),
                                repository.getMaxEatByEverOf(food),
                                Maybe.fromCallable(clock::get))
                        .firstElement();
            } else {
                return Maybe.just(clock.get().plus(food.expirationOffset()));
            }
        });
        Observable<Id<Location>> predictedLocation = foodObservable.flatMapMaybe(food -> food.location().map(Maybe::just)
                .orElseGet(() -> repository.getLocationWithMostItemsOfType(food)));

        Observable<List<LocationForSelection>> locations = repository.getLocations();
        Observable<List<ScaledUnitForSelection>> units = repository.getUnits();

        return Observable.zip(foodObservable, locations, units, predictedEatBy, predictedLocation, (f, l, u, eatBy, location) -> {
            int locationPosition = ListSearcher.searchFirst(l, location).orElse(0);
            int unitPosition = ListSearcher.findFirst(u, f.unit());
            return FoodItemAddData.create(f.toSelection(), localiser.toLocalDate(eatBy), l, locationPosition, u, unitPosition);
        });
    }

    @Override
    public void add(FoodItemForm item) {
        scheduler.schedule(Job.create(Job.Type.ADD_FOOD_ITEM, () -> addInBackground(item)));
    }

    void addInBackground(FoodItemForm item) {
        try {
            service.add(item.toNetwork());
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            LOG.warn("failed to add food " + item);
            errorRecorder.recordFoodItemAddError(e, item);
            synchroniser.synchroniseAfterError(e);
        }
    }
}
