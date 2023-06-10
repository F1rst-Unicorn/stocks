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
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static de.njsm.stocks.client.business.entities.IdImpl.create;
import static java.util.Collections.emptyList;
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
        assertEquals(R.string.error_details_food_edit_error_list, (long) uut.visit(FoodEditErrorDetails.create(1, "Banana", true, Period.ofDays(3), 4, 5, "yellow"), null));
        assertEquals(R.string.error_details_food_item_add_error_list, (long) uut.visit(FoodItemAddErrorDetails.create(LocalDate.ofEpochDay(2), 1, 2, 3, FoodItemAddErrorDetails.Unit.create(BigDecimal.ONE, "g"), "Banana", "Fridge"), null));
        assertEquals(R.string.error_details_food_item_delete_error_list, (long) uut.visit(FoodItemDeleteErrorDetails.create(1, "Banana", FoodItemDeleteErrorDetails.Unit.create(BigDecimal.ONE, "g")), null));
        assertEquals(R.string.error_details_food_item_edit_error_list, (long) uut.visit(FoodItemEditErrorDetails.create(1, "Banana", LocalDate.ofEpochDay(2), 3, 4), null));
        assertEquals(R.string.error_details_ean_number_add_error_list, (long) uut.visit(EanNumberAddErrorDetails.create(1, "Banana", "123"), null));
        assertEquals(R.string.error_details_ean_number_delete_error_list, (long) uut.visit(EanNumberDeleteErrorDetails.create(1, "Banana", "123"), null));
        assertEquals(R.string.error_details_user_device_delete_error_list, (long) uut.visit(UserDeviceDeleteErrorDetails.create(1, "Jack", "Mobile"), null));
        assertEquals(R.string.error_details_user_delete_error_list, (long) uut.visit(UserDeleteErrorDetails.create(1, "Jack"), null));
        assertEquals(R.string.error_details_recipe_add_error_list, (long) uut.visit(RecipeAddForm.create("Pizza", "just bake", Duration.ofMinutes(3), emptyList(), emptyList()), null));
        assertEquals(R.string.error_details_food_to_buy, (long) uut.visit(FoodForBuying.create(1, 2, false), null));
        assertEquals(R.string.error_details_user_add, (long) uut.visit(UserAddForm.create("Joanna"), null));
        assertEquals(R.string.error_details_user_device_add, (long) uut.visit(UserDeviceAddErrorDetails.create("Mobile", create(2), "Joanna"), null));
        assertEquals(R.string.error_details_recipe_delete, (long) uut.visit(RecipeDeleteErrorDetails.create(2, "Pizza"), null));
        assertEquals(R.string.error_details_recipe_edit, (long) uut.visit(RecipeEditForm.create(RecipeEditBaseData.create(1, "Pizza", "just bake", Duration.ofMinutes(2)), List.of(), List.of()), null));
    }
}
