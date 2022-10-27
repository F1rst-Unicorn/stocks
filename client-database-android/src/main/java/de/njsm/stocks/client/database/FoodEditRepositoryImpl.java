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

import de.njsm.stocks.client.business.FoodEditRepository;
import de.njsm.stocks.client.business.LocationRepository;
import de.njsm.stocks.client.business.ScaledUnitRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

class FoodEditRepositoryImpl implements FoodEditRepository {

    private final FoodDao foodDao;

    private final ScaledUnitRepository scaledUnitRepository;

    private final LocationRepository locationRepository;

    @Inject
    FoodEditRepositoryImpl(FoodDao foodDao, ScaledUnitRepository scaledUnitRepository, LocationRepository locationRepository) {
        this.foodDao = foodDao;
        this.scaledUnitRepository = scaledUnitRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public Observable<FoodToEdit> getFood(Id<Food> food) {
        return foodDao.getToEdit(food.id()).map(v ->
            FoodToEdit.create(
                    v.id(),
                    v.name(),
                    v.expirationOffset(),
                    v.location(),
                    v.storeUnit(),
                    v.description()
            )
        );
    }

    @Override
    public Observable<List<ScaledUnitForSelection>> getScaledUnitsForSelection() {
        return scaledUnitRepository.getScaledUnitsForSelection();
    }

    @Override
    public Observable<List<LocationForSelection>> getLocations() {
        return locationRepository.getLocationsForSelection();
    }

    @Override
    public FoodForEditing getFoodForSending(Id<Food> editedFood) {
        FoodDbEntity entity = foodDao.getForEditing(editedFood.id());
        return FoodForEditing.create(
                entity.id(),
                entity.version(),
                entity.name(),
                entity.expirationOffset(),
                Optional.ofNullable(entity.location()),
                entity.storeUnit(),
                entity.description()
        );
    }
}
