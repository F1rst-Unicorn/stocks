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

import de.njsm.stocks.client.business.entities.LocationForListing;
import de.njsm.stocks.client.business.entities.LocationForSelection;
import de.njsm.stocks.client.business.entities.ScaledUnitForListing;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Period;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FoodEditConflictFormDataTest {

    @Test
    void noSelectableLocationGivesEmptyOptional() {
        FoodEditConflictFormData uut = FoodEditConflictFormData.create(
                getFood(),
                singletonList(LocationForSelection.create(1, "not present")),
                singletonList(ScaledUnitForSelection.create(1, "not present", BigDecimal.ONE))
        );

        assertEquals(empty(), uut.currentLocationListPosition());
    }

    @Test
    void localLocationIsPreselected() {
        FoodEditConflictFormData uut = FoodEditConflictFormData.create(
                getFood(),
                asList(
                        LocationForSelection.create(7, "original location"),
                        LocationForSelection.create(8, "remote location"),
                        LocationForSelection.create(9, "local location")
                ),
                singletonList(ScaledUnitForSelection.create(1, "not present", BigDecimal.ONE))
        );

        assertEquals(of(2), uut.currentLocationListPosition());
    }

    @Test
    void missingLocalPreselectsRemote() {
        FoodEditConflictFormData uut = FoodEditConflictFormData.create(
                getFood(),
                asList(
                        LocationForSelection.create(7, "original location"),
                        LocationForSelection.create(8, "remote location")
                ),
                singletonList(ScaledUnitForSelection.create(1, "not present", BigDecimal.ONE))
        );

        assertEquals(of(1), uut.currentLocationListPosition());
    }

    @Test
    void missingRemotePreselectsOriginal() {
        FoodEditConflictFormData uut = FoodEditConflictFormData.create(
                getFood(),
                asList(
                        LocationForSelection.create(6, "unrelated"),
                        LocationForSelection.create(7, "original location")
                ),
                singletonList(ScaledUnitForSelection.create(1, "not present", BigDecimal.ONE))
        );

        assertEquals(of(1), uut.currentLocationListPosition());
    }

    private FoodEditConflictData getFood() {
        return FoodEditConflictData.create(1, 2, 3,
                "original name", "remote name", "local name",
                false, false, true,
                Period.ofDays(4), Period.ofDays(5), Period.ofDays(6),
                of(LocationForListing.create(7, "original location")),
                of(LocationForListing.create(8, "remote location")),
                of(LocationForListing.create(9, "local location")),
                ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                ScaledUnitForListing.create(12, "remote", BigDecimal.valueOf(12)),
                ScaledUnitForListing.create(13, "local", BigDecimal.valueOf(14)),
                "original description", "remote description", "local description");
    }
}