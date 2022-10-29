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
import de.njsm.stocks.client.business.ListSearcher;
import de.njsm.stocks.client.business.entities.*;

import java.time.LocalDate;
import java.util.List;

@AutoValue
public abstract class FoodItemEditConflictFormData implements Id<FoodItem> {

    public abstract int errorId();

    public abstract int originalVersion();

    public abstract String foodName();

    public abstract ConflictData<LocalDate> eatBy();

    public abstract ConflictData<LocationForListing> location();

    public abstract ConflictData<ScaledUnitForListing> unit();

    public abstract ListWithSuggestion<LocationForSelection> locations();

    public abstract ListWithSuggestion<ScaledUnitForSelection> units();

    public boolean hasAnyConflict() {
        return eatBy().needsHandling() ||
                location().needsHandling() ||
                unit().needsHandling();
    }

    public boolean hasNoConflict() {
        return !hasAnyConflict();
    }

    public static FoodItemEditConflictFormData create(
            FoodItemEditConflictData foodItem,
            List<LocationForSelection> locations,
            List<ScaledUnitForSelection> scaledUnits) {

        var selectedLocationPosition = ListWithSuggestion.create(locations,
                ListSearcher.searchFirst(locations, foodItem.storedIn()).orElse(0));
        var selectedScaledUnitPosition = ListWithSuggestion.create(scaledUnits,
                ListSearcher.searchFirst(scaledUnits, foodItem.unit()).orElse(0));

        return new AutoValue_FoodItemEditConflictFormData(foodItem.id(), foodItem.errorId(), foodItem.originalVersion(),
                foodItem.foodName(),
                foodItem.eatBy(),
                foodItem.storedIn(),
                foodItem.unit(),
                selectedLocationPosition,
                selectedScaledUnitPosition);
    }
}
