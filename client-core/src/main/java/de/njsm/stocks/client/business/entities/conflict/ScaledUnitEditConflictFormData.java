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
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.ScaledUnit;
import de.njsm.stocks.client.business.entities.UnitForListing;
import de.njsm.stocks.client.business.entities.UnitForSelection;

import java.math.BigDecimal;
import java.util.List;

@AutoValue
public abstract class ScaledUnitEditConflictFormData implements Id<ScaledUnit> {

    public abstract int errorId();

    public abstract int originalVersion();

    public abstract ConflictData<BigDecimal> scale();

    public abstract ConflictData<UnitForListing> unit();

    public abstract List<UnitForSelection> availableUnits();

    public abstract int currentUnitListPosition();

    public boolean hasAnyConflict() {
        return scale().needsHandling() || unit().needsHandling();
    }

    public boolean hasNoConflict() {
        return !hasAnyConflict();
    }

    public static ScaledUnitEditConflictFormData create(
            ScaledUnitEditConflictData scaledUnit,
            List<UnitForSelection> units) {

        int selectedListPosition = ListSearcher.searchFirst(units, scaledUnit.unit()).orElse(0);

        return new AutoValue_ScaledUnitEditConflictFormData(
                scaledUnit.id(),
                scaledUnit.errorId(),
                scaledUnit.originalVersion(),
                scaledUnit.scale(),
                scaledUnit.unit(),
                units,
                selectedListPosition);
    }
}
