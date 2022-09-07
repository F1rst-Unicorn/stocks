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

package de.njsm.stocks.client.business.entities;

import com.google.auto.value.AutoValue;

import java.time.Period;
import java.util.List;
import java.util.Optional;

@AutoValue
public abstract class FoodEditingFormData implements Identifiable<Food> {

    public abstract String name();

    public abstract Period expirationOffset();

    public abstract List<LocationForSelection> locations();

    public abstract Optional<Integer> currentLocationListPosition();

    public abstract List<ScaledUnitForSelection> storeUnits();

    public abstract int currentStoreUnitListPosition();

    public abstract String description();

    public static FoodEditingFormData create(int id, String name, Period expirationOffset, List<LocationForSelection> locations, Optional<Integer> currentLocationListPosition, List<ScaledUnitForSelection> storeUnits, int currentStoreUnitListPosition, String description) {
        return new AutoValue_FoodEditingFormData(id, name, expirationOffset, locations, currentLocationListPosition, storeUnits, currentStoreUnitListPosition, description);
    }
}
