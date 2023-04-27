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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.FoodListRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class FoodListRepositoryImpl implements FoodListRepository {

    private final FoodDao foodDao;

    private final PlotDao plotDao;

    private final LocationDao locationDao;

    @Inject
    FoodListRepositoryImpl(FoodDao foodDao, PlotDao plotDao, LocationDao locationDao) {
        this.foodDao = foodDao;
        this.plotDao = plotDao;
        this.locationDao = locationDao;
    }

    @Override
    public Observable<List<FoodForListingBaseData>> getFood() {
        return foodDao.getCurrentFood();
    }

    @Override
    public Observable<FoodDetailsBaseData> getFood(Id<Food> id) {
        return foodDao.getDetails(id.id())
                .map(v ->
                        FoodDetailsBaseData.create(
                                v.food.id(),
                                v.food.name(),
                                v.food.expirationOffset(),
                                Optional.ofNullable(v.locationName),
                                ScaledUnitForSelection.create(v.scaledUnitId, v.abbreviation, v.scale),
                                v.food.description()));
    }

    @Override
    public Observable<List<FoodForListingBaseData>> getFoodBy(Id<Location> location) {
        return foodDao.getCurrentFoodBy(location.id());
    }

    @Override
    public Observable<List<StoredFoodAmount>> getFoodAmounts() {
        return foodDao.getAmounts();
    }

    @Override
    public Observable<List<StoredFoodAmount>> getFoodAmounts(Id<Food> id) {
        return foodDao.getAmountsOf(id.id());
    }

    @Override
    public Observable<List<StoredFoodAmount>> getFoodAmountsIn(Id<Location> location) {
        return foodDao.getAmountsStoredIn(location.id());
    }

    @Override
    public Observable<LocationName> getLocationName(Id<Location> location) {
        return locationDao.getCurrentLocation(location.id())
                .map(v -> LocationName.create(v.name()));
    }

    @Override
    public Observable<List<FoodForEanNumberAssignment>> getForEanNumberAssignment() {
        return foodDao.getForEanNumberAssignment();
    }

    @Override
    public Observable<List<PlotByUnit<Instant>>> getAmountsOverTime(Id<Food> id) {

        final class PartialAggregate {
            final Id<Unit> unit;
            String abbreviation;
            final List<PlotPoint<Instant>> points;
            BigDecimal prefixSum;

            public PartialAggregate(PlotPoint<Instant> point, Id<Unit> unit, String abbreviation) {
                this.points = new ArrayList<>();
                this.points.add(point);
                this.unit = unit;
                this.abbreviation = abbreviation;
                this.prefixSum = point.y();
            }
        }

        return plotDao.getAmountsOverTimeOf(id.id())
                .map(list ->
                    StreamSupport.stream(new Aggregator<>(list.iterator(),
                            input -> new PartialAggregate(PlotPoint.create(input.x, input.y), input.unit, input.abbreviation),
                            (current, input) -> current.unit.id() == input.unit.id(),
                            (current, input) -> {
                                current.prefixSum = current.prefixSum.add(input.y);
                                current.points.add(PlotPoint.create(input.x, current.prefixSum));
                                current.abbreviation = input.abbreviation;
                                return current;
                        }), false)
                            .map(v -> PlotByUnit.create(IdImpl.from(v.unit), v.abbreviation, v.points))
                            .collect(Collectors.toList())
                );
    }

    @Override
    public Observable<List<PlotPoint<Integer>>> getEatByExpirationHistogram(Id<Food> id) {
        return plotDao.getEatByExpirationHistogram(id.id())
                .map(list -> list.stream()
                        .map(v -> PlotPoint.create(v.x, v.y))
                        .collect(Collectors.toList())
                );
    }
}
