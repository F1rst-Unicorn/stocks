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

package de.njsm.stocks.client.business.entities.conflict;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.entities.*;

import java.time.LocalDate;

@AutoValue
public abstract class FoodItemEditConflictData implements Id<FoodItem> {

    public abstract int errorId();

    public abstract int originalVersion();

    public abstract String foodName();

    public abstract ConflictData<LocalDate> eatBy();

    public abstract ConflictData<LocationForListing> storedIn();

    public abstract ConflictData<ScaledUnitForListing> unit();

    public static FoodItemEditConflictData create(
            int errorId,
            int id,
            int originalVersion,
            String foodName,
            LocalDate originalEatBy,
            LocalDate remoteEatBy,
            LocalDate localEatBy,
            LocationForListing originalLocation,
            LocationForListing remoteLocation,
            LocationForListing localLocation,
            ScaledUnitForListing originalUnit,
            ScaledUnitForListing remoteUnit,
            ScaledUnitForListing localUnit) {
        return new AutoValue_FoodItemEditConflictData(id, errorId, originalVersion, foodName,
                ConflictData.create(originalEatBy, remoteEatBy, localEatBy),
                ConflictData.create(originalLocation, remoteLocation, localLocation),
                ConflictData.create(originalUnit, remoteUnit, localUnit));
    }
}
