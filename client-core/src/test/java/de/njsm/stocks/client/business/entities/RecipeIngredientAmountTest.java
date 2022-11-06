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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

class RecipeIngredientAmountTest {

    private RecipeIngredientAmount uut;

    @AfterEach
    void tearDown() {
        sufficientImpliesNecessary();
    }

    @Test
    void noPresentAmountIsNotSufficient() {
        uut = getUut(RecipeIngredientAmount.Amount.create(3, BigDecimal.ONE, 1), emptyList());

        assertFalse(uut.isSufficientAmountPresent());
        assertFalse(uut.isNecessaryAmountPresent());
    }

    @Test
    void anyPresentAmountIsNecessaryButNotSufficient() {
        uut = getUut(RecipeIngredientAmount.Amount.create(3, BigDecimal.ONE, 1), List.of(
                RecipeIngredientAmount.Amount.create(4, BigDecimal.ONE, 1)
        ));

        assertFalse(uut.isSufficientAmountPresent());
        assertTrue(uut.isNecessaryAmountPresent());
    }

    @Test
    void tooLittlePresentAmountIsNecessaryButNotSufficient() {
        uut = getUut(RecipeIngredientAmount.Amount.create(3, BigDecimal.ONE, 2), List.of(
                RecipeIngredientAmount.Amount.create(3, BigDecimal.ONE, 1)
        ));

        assertFalse(uut.isSufficientAmountPresent());
        assertTrue(uut.isNecessaryAmountPresent());
    }

    @Test
    void enoughPresentAmountOfCorrectUnitIsNecessaryButNotSufficient() {
        uut = getUut(RecipeIngredientAmount.Amount.create(3, BigDecimal.ONE, 2), List.of(
                RecipeIngredientAmount.Amount.create(3, BigDecimal.ONE, 2)
        ));

        assertTrue(uut.isSufficientAmountPresent());
        assertTrue(uut.isNecessaryAmountPresent());
    }

    @Test
    void enoughSummableAmountsAreSufficient() {
        uut = getUut(RecipeIngredientAmount.Amount.create(3, BigDecimal.ONE, 3), List.of(
                RecipeIngredientAmount.Amount.create(3, BigDecimal.ONE, 1),
                RecipeIngredientAmount.Amount.create(3, BigDecimal.valueOf(2), 1)
        ));

        assertTrue(uut.isSufficientAmountPresent());
        assertTrue(uut.isNecessaryAmountPresent());
    }

    @Test
    void moreSummableAmountsAreSufficient() {
        uut = getUut(RecipeIngredientAmount.Amount.create(3, BigDecimal.ONE, 3), List.of(
                RecipeIngredientAmount.Amount.create(3, BigDecimal.ONE, 2),
                RecipeIngredientAmount.Amount.create(3, BigDecimal.valueOf(2), 1)
        ));

        assertTrue(uut.isSufficientAmountPresent());
        assertTrue(uut.isNecessaryAmountPresent());
    }

    void sufficientImpliesNecessary() {
        assertTrue(!uut.isSufficientAmountPresent() || uut.isNecessaryAmountPresent());
    }

    private static RecipeIngredientAmount getUut(RecipeIngredientAmount.Amount required, List<RecipeIngredientAmount.Amount> present) {
        return RecipeIngredientAmount.create(2, required, present);
    }
}