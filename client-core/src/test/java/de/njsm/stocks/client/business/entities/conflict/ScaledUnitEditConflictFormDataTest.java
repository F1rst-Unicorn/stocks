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

import de.njsm.stocks.client.business.entities.UnitForListing;
import de.njsm.stocks.client.business.entities.UnitForSelection;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ScaledUnitEditConflictFormDataTest {

    @Test
    void noSelectableUnitGivesOnlyUnitWhichIsAlwaysPresent() {
        ScaledUnitEditConflictFormData uut = ScaledUnitEditConflictFormData.create(
                getScaledUnit(),
                singletonList(UnitForSelection.create(6, "original name"))
        );

        assertEquals(0, uut.currentUnitListPosition());
    }

    @Test
    void localUnitIsPreselected() {
        ScaledUnitEditConflictFormData uut = ScaledUnitEditConflictFormData.create(
                getScaledUnit(),
                asList(
                        UnitForSelection.create(7, "original name"),
                        UnitForSelection.create(8, "remote name"),
                        UnitForSelection.create(9, "local name")
                )
        );

        assertEquals(2, uut.currentUnitListPosition());
    }

    @Test
    void missingLocalUnitPreselectsRemote() {
        ScaledUnitEditConflictFormData uut = ScaledUnitEditConflictFormData.create(
                getScaledUnit(),
                asList(
                        UnitForSelection.create(7, "original name"),
                        UnitForSelection.create(8, "remote name")
                )
        );

        assertEquals(1, uut.currentUnitListPosition());
    }

    @Test
    void missingRemoteUnitPreselectsOriginal() {
        ScaledUnitEditConflictFormData uut = ScaledUnitEditConflictFormData.create(
                getScaledUnit(),
                asList(
                        UnitForSelection.create(6, "unrelated"),
                        UnitForSelection.create(7, "original name")
                )
        );

        assertEquals(1, uut.currentUnitListPosition());
    }

    private ScaledUnitEditConflictData getScaledUnit() {
        return ScaledUnitEditConflictData.create(1, 2, 3, BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(6),
                UnitForListing.create(7, "original name", "original abbreviation"),
                UnitForListing.create(8, "remote name", "remote abbreviation"),
                UnitForListing.create(9, "local name", "local abbreviation")
        );
    }
}