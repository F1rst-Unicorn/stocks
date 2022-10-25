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

import de.njsm.stocks.client.business.FoodItemAddRepository;
import de.njsm.stocks.client.business.LocationRepository;
import de.njsm.stocks.client.business.ScaledUnitRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

class FoodItemAddRepositoryImpl implements FoodItemAddRepository {

    private final ScaledUnitRepository scaledUnitRepository;

    private final LocationRepository locationRepository;

    private final FoodItemDao foodItemDao;

    private final FoodDao foodDao;

    @Inject
    FoodItemAddRepositoryImpl(ScaledUnitRepository scaledUnitRepository, LocationRepository locationRepository, FoodItemDao foodItemDao, FoodDao foodDao) {
        this.scaledUnitRepository = scaledUnitRepository;
        this.locationRepository = locationRepository;
        this.foodItemDao = foodItemDao;
        this.foodDao = foodDao;
    }

    @Override
    public Observable<FoodForItemCreation> getFood(Identifiable<Food> food) {
        return foodDao.getToEdit(food.id())
                .map(f -> FoodForItemCreation.create(
                        f.id(),
                        f.name(),
                        f.expirationOffset(),
                        Optional.ofNullable(f.location()).map(v -> () -> v),
                        f::storeUnit
                ));
    }

    @Override
    public Observable<List<LocationForSelection>> getLocations() {
        return locationRepository.getLocationsForSelection();
    }

    @Override
    public Observable<List<ScaledUnitForSelection>> getUnits() {
        return scaledUnitRepository.getScaledUnitsForSelection();
    }

    @Override
    public Maybe<Instant> getMaxEatByOfPresentItemsOf(Identifiable<Food> food) {
        return foodItemDao.getMaxEatByOfPresentItemsOf(food.id());
    }

    @Override
    public Maybe<Instant> getMaxEatByEverOf(Identifiable<Food> food) {
        return foodItemDao.getMaxEatByEverOf(food.id());
    }

    @Override
    public Maybe<Identifiable<Location>> getLocationWithMostItemsOfType(Identifiable<Food> food) {
        return foodItemDao.getLocationWithMostItemsOfType(food.id()).map(v -> () -> v);
    }
}
