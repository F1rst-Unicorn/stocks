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

package de.njsm.stocks.client.databind;

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.ui.R;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import static org.junit.Assert.assertEquals;

public class ErrorDetailsHeadlineVisitorTest {

    private ErrorDetailsHeadlineVisitor uut;

    @Before
    public void setup() {
        uut = new ErrorDetailsHeadlineVisitor();
    }

    @Test
    public void headlinesAreMappedCorrectly() {
        assertEquals(R.string.error_details_location_add_error_list, (long) uut.visit(LocationAddForm.create("name", "description"), null));
        assertEquals(R.string.error_details_synchronisation_error_list, (long) uut.visit(SynchronisationErrorDetails.create(), null));
        assertEquals(R.string.error_details_location_delete_error_list, (long) uut.visit(LocationDeleteErrorDetails.create(2, "name"), null));
        assertEquals(R.string.error_details_location_edit_error_list, (long) uut.visit(LocationEditErrorDetails.create(2, "name", "description"), null));
        assertEquals(R.string.error_details_unit_add_error_list, (long) uut.visit(UnitAddForm.create("name", "abbreviation"), null));
        assertEquals(R.string.error_details_unit_delete_error_list, (long) uut.visit(UnitDeleteErrorDetails.create(1, "name", "abbreviation"), null));
        assertEquals(R.string.error_details_unit_edit_error_list, (long) uut.visit(UnitEditErrorDetails.create(1, "name", "abbreviation"), null));
        assertEquals(R.string.error_details_scaled_unit_add_error_list, (long) uut.visit(ScaledUnitAddErrorDetails.create(BigDecimal.ONE, 2, "name", "abbreviation"), null));
        assertEquals(R.string.error_details_scaled_unit_edit_error_list, (long) uut.visit(ScaledUnitEditErrorDetails.create(1, BigDecimal.ONE, 2, "name", "abbreviation"), null));
        assertEquals(R.string.error_details_scaled_unit_delete_error_list, (long) uut.visit(ScaledUnitDeleteErrorDetails.create(1, BigDecimal.ONE, "name", "abbreviation"), null));
        assertEquals(R.string.error_details_scaled_unit_delete_error_list, (long) uut.visit(ScaledUnitDeleteErrorDetails.create(1, BigDecimal.ONE, "name", "abbreviation"), null));
        assertEquals(R.string.error_details_food_add_error_list, (long) uut.visit(FoodAddErrorDetails.create(
                "Banana",
                true,
                Period.ZERO,
                null,
                2,
                "they are yellow",
                "",
                FoodAddErrorDetails.StoreUnit.create(BigDecimal.TEN, "g")), null));
        assertEquals(R.string.error_details_food_delete_error_list, (long) uut.visit(FoodDeleteErrorDetails.create(1, "Banana"), null));
        assertEquals(R.string.error_details_food_edit_error_list, (long) uut.visit(FoodEditErrorDetails.create(1, "Banana", Period.ofDays(3), 4, 5, "yellow"), null));
        assertEquals(R.string.error_details_food_item_add_error_list, (long) uut.visit(FoodItemAddErrorDetails.create(LocalDate.ofEpochDay(2), 1, 2, 3, FoodItemAddErrorDetails.Unit.create(BigDecimal.ONE, "g"), "Banana", "Fridge"), null));
    }
}
