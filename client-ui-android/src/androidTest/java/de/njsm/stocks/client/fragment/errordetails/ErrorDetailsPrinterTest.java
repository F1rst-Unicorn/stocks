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

package de.njsm.stocks.client.fragment.errordetails;

import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.ui.R;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.client.business.entities.IdImpl.create;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class ErrorDetailsPrinterTest {

    private ErrorDetailsFragment.ErrorDetailsPrinter uut;

    private Function<Integer, String> dictionary;

    @Before
    public void setup() {
        dictionary = ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext())::getString;
        uut = new ErrorDetailsFragment.ErrorDetailsPrinter(dictionary);
    }

    @Test
    public void synchronisationErrorIsEmpty() {
        SynchronisationErrorDetails data = SynchronisationErrorDetails.create();
        assertEquals("", uut.visit(data, null));
    }

    @Test
    public void locationAddErrorShowsNameAndDescription() {
        LocationAddForm data = LocationAddForm.create("name", "description");
        assertEquals(data.name() + "\n" + data.description(), uut.visit(data, null));
    }

    @Test
    public void locationDeleteErrorShowsName() {
        LocationDeleteErrorDetails data = LocationDeleteErrorDetails.create(2, "name");
        assertEquals(data.name(), uut.visit(data, null));
    }

    @Test
    public void locationEditErrorShowsName() {
        LocationEditErrorDetails data = LocationEditErrorDetails.create(2, "name", "description");
        assertEquals(data.name(), uut.visit(data, null));
    }

    @Test
    public void unitAddErrorShowsName() {
        UnitAddForm data = UnitAddForm.create("Gramm", "g");
        assertEquals(data.name() + " (" + data.abbreviation() + ")", uut.visit(data, null));
    }

    @Test
    public void unitDeleteErrorShowsName() {
        UnitDeleteErrorDetails data = UnitDeleteErrorDetails.create(1, "Gramm", "g");
        assertEquals(data.name() + " (" + data.abbreviation() + ")", uut.visit(data, null));
    }

    @Test
    public void unitEditErrorShowsName() {
        UnitEditErrorDetails data = UnitEditErrorDetails.create(1, "Gramm", "g");
        assertEquals(data.name() + " (" + data.abbreviation() + ")", uut.visit(data, null));
    }

    @Test
    public void scaledUnitAddErrorShowsScaleAndUnit() {
        ScaledUnitAddErrorDetails data = ScaledUnitAddErrorDetails.create(BigDecimal.TEN.pow(3), 1, "Gramm", "g");
        assertEquals("1kg (Gramm)", uut.visit(data, null));
    }

    @Test
    public void scaledUnitEditErrorShowsScaleAndUnit() {
        ScaledUnitEditErrorDetails data = ScaledUnitEditErrorDetails.create(1, BigDecimal.TEN.pow(3), 1, "Gramm", "g");
        assertEquals("1kg (Gramm)", uut.visit(data, null));
    }

    @Test
    public void scaledUnitDeleteErrorShowsScaleAndUnit() {
        ScaledUnitDeleteErrorDetails data = ScaledUnitDeleteErrorDetails.create(1, BigDecimal.TEN.pow(3), "Gramm", "g");
        assertEquals("1kg (Gramm)", uut.visit(data, null));
    }

    @Test
    public void foodAddErrorShowsDetails() {
        ScaledUnitDeleteErrorDetails data = ScaledUnitDeleteErrorDetails.create(1, BigDecimal.TEN.pow(3), "Gramm", "g");
        assertEquals("1kg (Gramm)", uut.visit(data, null));
    }

    @Test
    public void foodAddingShowsDetails() {
        FoodAddErrorDetails data = FoodAddErrorDetails.create(
                "Banana",
                true,
                Period.ZERO,
                null,
                2,
                "",
                "",
                FoodAddErrorDetails.StoreUnit.create(BigDecimal.TEN, "g"));
        assertEquals("Banana (10g)", uut.visit(data, null));

        data = FoodAddErrorDetails.create(
                "Banana",
                true,
                Period.ZERO,
                1,
                2,
                "they are yellow",
                "Cupboard",
                FoodAddErrorDetails.StoreUnit.create(BigDecimal.TEN, "g"));
        assertEquals("Banana (Cupboard, 10g)\nthey are yellow", uut.visit(data, null));
    }

    @Test
    public void foodDeleteErrorShowsName() {
        FoodDeleteErrorDetails data = FoodDeleteErrorDetails.create(1, "Banana");
        assertEquals(data.name(), uut.visit(data, null));
    }

    @Test
    public void foodEditErrorShowsName() {
        FoodEditErrorDetails data = FoodEditErrorDetails.create(1, "Banana", true, Period.ofDays(3), 4, 5, "yellow");
        assertEquals(data.name(), uut.visit(data, null));
    }

    @Test
    public void foodItemAddingShowsAllDetails() {
        FoodItemAddErrorDetails data = FoodItemAddErrorDetails.create(LocalDate.ofEpochDay(2), 1, 2, 3, FoodItemAddErrorDetails.Unit.create(BigDecimal.ONE, "g"), "Banana", "Fridge");
        assertEquals("1g Banana\nFridge", uut.visit(data, null));
    }

    @Test
    public void foodItemDeletingShowsDetails() {
        FoodItemDeleteErrorDetails data = FoodItemDeleteErrorDetails.create(1, "Banana", FoodItemDeleteErrorDetails.Unit.create(BigDecimal.ONE, "g"));
        assertEquals("1g Banana", uut.visit(data, null));
    }

    @Test
    public void foodItemEditingShowsDetails() {
        FoodItemEditErrorDetails data = FoodItemEditErrorDetails.create(1, "Banana", LocalDate.ofEpochDay(2), 3, 4);
        assertEquals(data.foodName(), uut.visit(data, null));
    }

    @Test
    public void eanNumberAddingShowsDetails() {
        EanNumberAddErrorDetails data = EanNumberAddErrorDetails.create(1, "Banana", "123");
        assertEquals("Banana (123)", uut.visit(data, null));
    }

    @Test
    public void eanNumberDeletingShowsDetails() {
        EanNumberDeleteErrorDetails data = EanNumberDeleteErrorDetails.create(1, "Banana", "123");
        assertEquals("Banana (123)", uut.visit(data, null));
    }

    @Test
    public void userDeviceDeletingShowsDetails() {
        UserDeviceDeleteErrorDetails data = UserDeviceDeleteErrorDetails.create(1, "Jack", "Mobile");
        assertEquals(String.format(dictionary.apply(R.string.error_details_user_device_format), data.userName(), data.deviceName()), uut.visit(data, null));
    }

    @Test
    public void userDeletingShowsDetails() {
        UserDeleteErrorDetails data = UserDeleteErrorDetails.create(1, "Jack");
        assertEquals(data.name(), uut.visit(data, null));
    }

    @Test
    public void recipeAddingShowsName() {
        var recipe = RecipeAddForm.create("Pizza", "just bake", Duration.ofMinutes(3), emptyList(), emptyList());
        assertEquals(recipe.name(), uut.visit(recipe, null));
    }

    @Test
    public void userAddingShowsName() {
        var user = UserAddForm.create("Joanna");
        assertEquals(user.name(), uut.visit(user, null));
    }

    @Test
    public void userDeviceAddingShowsName() {
        var device = UserDeviceAddErrorDetails.create("Mobile", create(2), "Joanna");
        assertEquals(device.name() + " (" + device.ownerName() + ")", uut.visit(device, null));
    }

    @Test
    public void recipeDeletingShowsName() {
        var recipe = RecipeDeleteErrorDetails.create(2, "Pizza");
        assertEquals(recipe.name(), uut.visit(recipe, null));
    }

    @Test
    public void recipeEditingShowsName() {
        RecipeEditForm recipe = RecipeEditForm.create(
                RecipeEditBaseData.create(1, "Pizza", "just bake", Duration.ofMinutes(2)),
                List.of(),
                List.of());
        assertEquals(recipe.recipe().name(), uut.visit(recipe, null));
    }
}
