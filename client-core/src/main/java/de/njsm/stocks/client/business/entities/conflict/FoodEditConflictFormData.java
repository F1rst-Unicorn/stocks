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

import java.time.Period;
import java.util.List;
import java.util.Optional;

@AutoValue
public abstract class FoodEditConflictFormData implements Id<Food> {

    public abstract int errorId();

    public abstract int originalVersion();

    public abstract ConflictData<String> name();

    public abstract ConflictData<Period> expirationOffset();

    public abstract ConflictData<Optional<LocationForListing>> location();

    public abstract ConflictData<ScaledUnitForListing> storeUnit();

    public abstract ConflictData<String> description();

    public abstract List<LocationForSelection> availableLocations();

    public abstract List<ScaledUnitForSelection> availableStoreUnits();

    public abstract Optional<Integer> currentLocationListPosition();

    public abstract int currentScaledUnitListPosition();

    public boolean hasAnyConflict() {
        return name().needsHandling() ||
                expirationOffset().needsHandling() ||
                location().needsHandling() ||
                storeUnit().needsHandling() ||
                description().needsHandling();
    }

    public boolean hasNoConflict() {
        return !hasAnyConflict();
    }

    public static FoodEditConflictFormData create(
            FoodEditConflictData food,
            List<LocationForSelection> locations,
            List<ScaledUnitForSelection> scaledUnits) {

        Optional<Integer> selectedLocationPosition = ListSearcher.searchFirstOptional(locations, food.location());
        int selectedScaledUnitPosition = ListSearcher.searchFirst(scaledUnits, food.storeUnit()).orElse(0);

        return new AutoValue_FoodEditConflictFormData(food.id(), food.errorId(), food.originalVersion(),
                food.name(),
                food.expirationOffset(),
                food.location(),
                food.storeUnit(),
                food.description(),
                locations,
                scaledUnits,
                selectedLocationPosition,
                selectedScaledUnitPosition);
    }
}
