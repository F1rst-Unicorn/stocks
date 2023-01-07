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

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.FoodItemEditRepository;
import de.njsm.stocks.client.business.LocationRepository;
import de.njsm.stocks.client.business.ScaledUnitRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

class FoodItemEditRepositoryImpl implements FoodItemEditRepository {

    private final ScaledUnitRepository scaledUnitRepository;

    private final LocationRepository locationRepository;

    private final FoodItemDao foodItemDao;

    @Inject
    FoodItemEditRepositoryImpl(ScaledUnitRepository scaledUnitRepository,
                               LocationRepository locationRepository,
                               FoodItemDao foodItemDao) {
        this.scaledUnitRepository = scaledUnitRepository;
        this.locationRepository = locationRepository;
        this.foodItemDao = foodItemDao;
    }


    @Override
    public Observable<FoodItemEditBaseData> getFoodItem(Id<FoodItem> id) {
        return foodItemDao.getItemToEdit(id.id()).map(v ->
                FoodItemEditBaseData.create(
                        v.id(),
                        FoodForSelection.create(v.ofType(), v.foodName()),
                        v.eatBy(),
                        v.storedIn(),
                        v.unit()
                ));
    }

    @Override
    public Observable<List<LocationForSelection>> getLocations() {
        return locationRepository.getLocationsForSelection();
    }

    @Override
    public Observable<List<ScaledUnitForSelection>> getScaledUnits() {
        return scaledUnitRepository.getScaledUnitsForSelection();
    }

    @Override
    public FoodItemForEditing getFoodItemForSending(Id<FoodItem> editedFoodItem) {
        return foodItemDao.getCurrentItemForEditing(editedFoodItem.id());
    }

    @AutoValue
    public static abstract class FoodItemEditRecord implements Id<FoodItem> {

        public abstract int ofType();

        public abstract String foodName();

        public abstract Instant eatBy();

        public abstract int storedIn();

        public abstract int unit();

        public static FoodItemEditRecord create(int id, int ofType, String foodName, Instant eatBy, int storedIn, int unit) {
            return new AutoValue_FoodItemEditRepositoryImpl_FoodItemEditRecord(id, ofType, foodName, eatBy, storedIn, unit);
        }
    }
}
