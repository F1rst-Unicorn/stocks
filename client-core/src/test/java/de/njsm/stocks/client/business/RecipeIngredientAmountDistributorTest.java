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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.ScaledUnit;
import de.njsm.stocks.client.business.entities.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.njsm.stocks.client.business.entities.IdImpl.create;
import static java.math.BigDecimal.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RecipeIngredientAmountDistributorTest {

    private RecipeIngredientAmountDistributor uut;

    @BeforeEach
    void setUp() {
        uut = new RecipeIngredientAmountDistributor();
    }

    @Test
    public void oneMatchingFoodIsDistributed() {
        IdImpl<Unit> unit = create(1);
        IdImpl<ScaledUnit> scaledUnit = create(1);
        var required = RecipeIngredientAmountDistributor.RequiredAmount.create(unit, valueOf(5));
        var present = List.of(RecipeIngredientAmountDistributor.PresentAmount.create(unit, scaledUnit, valueOf(1), 5));

        var distribution = uut.distribute(required, present);

        assertThat(distribution, is(Map.of(scaledUnit, 5)));
    }

    @Test
    public void oneMatchingFoodOfDifferentScaleIsDistributed() {
        IdImpl<Unit> unit = create(1);
        IdImpl<ScaledUnit> scaledUnit = create(1);
        var required = RecipeIngredientAmountDistributor.RequiredAmount.create(unit, valueOf(5));
        var present = List.of(RecipeIngredientAmountDistributor.PresentAmount.create(unit, scaledUnit, valueOf(5), 1));

        var distribution = uut.distribute(required, present);

        assertThat(distribution, is(Map.of(scaledUnit, 1)));
    }

    @Test
    public void noMatchingFoodGivesEmptyList() {
        IdImpl<Unit> unit = create(1);
        IdImpl<ScaledUnit> scaledUnit = create(1);
        var required = RecipeIngredientAmountDistributor.RequiredAmount.create(unit, valueOf(5));
        List<RecipeIngredientAmountDistributor.PresentAmount> present = List.of();

        var distribution = uut.distribute(required, present);

        assertThat(distribution, is(Map.of()));
    }

    @Test
    public void noMatchingFoodOfSameUnitGivesAZeroEntry() {
        IdImpl<Unit> unit = create(1);
        IdImpl<ScaledUnit> scaledUnit = create(1);
        var required = RecipeIngredientAmountDistributor.RequiredAmount.create(unit, valueOf(5));
        var present = List.of(RecipeIngredientAmountDistributor.PresentAmount.create(create(2), scaledUnit, valueOf(1), 5));

        var distribution = uut.distribute(required, present);

        assertThat(distribution, is(Map.of(scaledUnit, 0)));
    }

    @Test
    public void compositeUnitsMatchExactly() {
        IdImpl<Unit> unit = create(1);
        IdImpl<ScaledUnit> scaledUnit = create(1);
        IdImpl<ScaledUnit> differentScaledUnit = create(2);
        var required = RecipeIngredientAmountDistributor.RequiredAmount.create(unit, valueOf(5));
        var present = List.of(
                RecipeIngredientAmountDistributor.PresentAmount.create(unit, scaledUnit, valueOf(1), 2),
                RecipeIngredientAmountDistributor.PresentAmount.create(unit, differentScaledUnit, valueOf(1), 3));

        var distribution = uut.distribute(required, present);

        assertThat(distribution, is(Map.of(scaledUnit, 2, differentScaledUnit, 3)));
    }

    @Test
    public void compositeUnitsOfDifferentScaleMatchExactly() {
        IdImpl<Unit> unit = create(1);
        IdImpl<ScaledUnit> scaledUnit = create(1);
        IdImpl<ScaledUnit> doubleScaledUnit = create(2);
        var required = RecipeIngredientAmountDistributor.RequiredAmount.create(unit, valueOf(5));
        var present = List.of(
                RecipeIngredientAmountDistributor.PresentAmount.create(unit, scaledUnit, valueOf(1), 5),
                RecipeIngredientAmountDistributor.PresentAmount.create(unit, doubleScaledUnit, valueOf(2), 5));

        var distribution = uut.distribute(required, present);

        assertThat(distribution, is(Map.of(scaledUnit, 1, doubleScaledUnit, 2)));
    }

    @Test
    public void lessInStockThanRequiredIsOk() {
        IdImpl<Unit> unit = create(1);
        IdImpl<ScaledUnit> scaledUnit = create(1);
        IdImpl<ScaledUnit> doubleScaledUnit = create(2);
        var required = RecipeIngredientAmountDistributor.RequiredAmount.create(unit, valueOf(20));
        var present = List.of(
                RecipeIngredientAmountDistributor.PresentAmount.create(unit, scaledUnit, valueOf(1), 10),
                RecipeIngredientAmountDistributor.PresentAmount.create(unit, doubleScaledUnit, valueOf(3), 1));

        var distribution = uut.distribute(required, present);

        assertThat(distribution, is(Map.of(scaledUnit, 10, doubleScaledUnit, 1)));
    }

    @Test
    public void interjectedDifferentUnitIsIgnored() {
        IdImpl<Unit> unit = create(1);
        IdImpl<Unit> otherUnit = create(2);
        IdImpl<ScaledUnit> scaledUnit = create(1);
        IdImpl<ScaledUnit> doubleScaledUnit = create(2);
        IdImpl<ScaledUnit> otherScaledUnit = create(3);
        var required = RecipeIngredientAmountDistributor.RequiredAmount.create(unit, valueOf(20));
        var present = List.of(
                RecipeIngredientAmountDistributor.PresentAmount.create(unit, scaledUnit, valueOf(1), 10),
                RecipeIngredientAmountDistributor.PresentAmount.create(otherUnit, otherScaledUnit, valueOf(1), 10),
                RecipeIngredientAmountDistributor.PresentAmount.create(unit, doubleScaledUnit, valueOf(3), 1));

        var distribution = uut.distribute(required, present);

        assertThat(distribution, is(Map.of(scaledUnit, 10, doubleScaledUnit, 1, otherScaledUnit, 0)));
    }

    @Test
    public void oversufficingAmountIsOnlyTakenAsNeeded() {
        IdImpl<Unit> unit = create(1);
        IdImpl<ScaledUnit> scaledUnit = create(1);
        IdImpl<ScaledUnit> secondScaledUnit = create(2);
        IdImpl<ScaledUnit> thirdScaledUnit = create(3);
        var required = RecipeIngredientAmountDistributor.RequiredAmount.create(unit, valueOf(100));
        var present = List.of(
                RecipeIngredientAmountDistributor.PresentAmount.create(unit, scaledUnit, valueOf(75), 2),
                RecipeIngredientAmountDistributor.PresentAmount.create(unit, secondScaledUnit, valueOf(10), 3),
                RecipeIngredientAmountDistributor.PresentAmount.create(unit, thirdScaledUnit, valueOf(1), 3));

        var distribution = uut.distribute(required, present);

        assertThat(distribution, is(Map.of(scaledUnit, 1, secondScaledUnit, 2, thirdScaledUnit, 3)));
    }
}