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
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class FoodDetailsInteractorImpl implements FoodDetailsInteractor {

    private final FoodListRepository repository;

    private final FoodRegrouper regrouper;

    private final Localiser localiser;

    private final Clock clock;

    @Inject
    FoodDetailsInteractorImpl(FoodListRepository repository, FoodRegrouper regrouper, Localiser localiser, Clock clock) {
        this.repository = repository;
        this.regrouper = regrouper;
        this.localiser = localiser;
        this.clock = clock;
    }

    @Override
    public Observable<FoodDetails> get(Id<Food> id) {
        return Observable.combineLatest(repository.getFood(id),
                repository.getFoodAmounts(id),
                repository.getAmountsOverTime(id),
                repository.getEatByExpirationHistogram(id),
                (food, amounts, amountsOverTime, expirationHistogram) ->
                FoodDetails.create(food,
                        food.name(),
                        food.expirationOffset(),
                        food.locationName(),
                        food.storeUnit(),
                        food.description(),
                        regrouper.regroupSingleFood(amounts),
                        amountsOverTime.stream()
                                .map(v -> PlotByUnit.create(v, v.abbreviation(), transformPlotPoints(v.plotPoints())))
                                .collect(Collectors.toList()),
                        expirationHistogram));
    }

    private List<PlotPoint<LocalDateTime>> transformPlotPoints(List<PlotPoint<Instant>> plotPoints) {
        ArrayList<PlotPoint<LocalDateTime>> result = new ArrayList<>();
        if (plotPoints.isEmpty()) {
            return result;
        }

        PlotPoint<Instant> firstPoint = plotPoints.get(0);
        result.add(PlotPoint.create(localiser.toLocalDateTime(firstPoint.x().minusNanos(1)), BigDecimal.ZERO));

        plotPoints.stream()
                .map(p -> PlotPoint.create(localiser.toLocalDateTime(p.x()), p.y()))
                .collect(Collectors.toCollection(() -> result));

        PlotPoint<Instant> lastPoint = plotPoints.get(plotPoints.size() - 1);
        result.add(PlotPoint.create(localiser.toLocalDateTime(clock.get()), lastPoint.y()));
        return result;
    }
}
