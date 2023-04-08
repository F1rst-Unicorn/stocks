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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.ScaledUnitRepository;
import de.njsm.stocks.client.business.entities.ScaledUnit;
import de.njsm.stocks.client.business.entities.ScaledUnitForListing;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import de.njsm.stocks.client.business.entities.Versionable;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class ScaledUnitRepositoryImplTest extends DbTestCase {

    private ScaledUnitRepository uut;

    @Before
    public void setUp() {
        uut = new ScaledUnitRepositoryImpl(stocksDatabase.scaledUnitDao());
    }

    @Test
    public void gettingScaledUnitsWorks() {
        UnitDbEntity unit = standardEntities.unitDbEntity();
        ScaledUnitDbEntity scaledUnit = standardEntities.scaledUnitDbEntityBuilder().unit(unit.id()).build();
        stocksDatabase.synchronisationDao().synchroniseUnits(singletonList(unit));
        stocksDatabase.synchronisationDao().synchroniseScaledUnits(singletonList(scaledUnit));

        Observable<List<ScaledUnitForListing>> actual = uut.getScaledUnits();

        testList(actual).assertValue(singletonList(ScaledUnitForListing.create(scaledUnit.id(), unit.abbreviation(), scaledUnit.scale())));
    }

    @Test
    public void gettingScaledUnitsSortsByScale() {
        UnitDbEntity unit = standardEntities.unitDbEntity();
        ScaledUnitDbEntity scaledUnit1 = standardEntities.scaledUnitDbEntityBuilder()
                .scale(BigDecimal.ONE)
                .unit(unit.id()).build();
        ScaledUnitDbEntity scaledUnit2 = standardEntities.scaledUnitDbEntityBuilder()
                .scale(new BigDecimal("2"))
                .unit(unit.id()).build();
        ScaledUnitDbEntity scaledUnit3 = standardEntities.scaledUnitDbEntityBuilder()
                .scale(new BigDecimal("10"))
                .unit(unit.id()).build();
        stocksDatabase.synchronisationDao().synchroniseUnits(singletonList(unit));
        stocksDatabase.synchronisationDao().synchroniseScaledUnits(List.of(scaledUnit1, scaledUnit2, scaledUnit3));

        Observable<List<ScaledUnitForListing>> actual = uut.getScaledUnits();

        testList(actual).assertValue(List.of(
                ScaledUnitForListing.create(scaledUnit1.id(), unit.abbreviation(), scaledUnit1.scale()),
                ScaledUnitForListing.create(scaledUnit2.id(), unit.abbreviation(), scaledUnit2.scale()),
                ScaledUnitForListing.create(scaledUnit3.id(), unit.abbreviation(), scaledUnit3.scale())
        ));
    }

    @Test
    public void gettingScaledUnitForDeletionWorks() {
        UnitDbEntity unit = standardEntities.unitDbEntity();
        ScaledUnitDbEntity scaledUnit = standardEntities.scaledUnitDbEntityBuilder().unit(unit.id()).build();
        stocksDatabase.synchronisationDao().synchroniseUnits(singletonList(unit));
        stocksDatabase.synchronisationDao().synchroniseScaledUnits(singletonList(scaledUnit));

        Versionable<ScaledUnit> actual = uut.getEntityForDeletion(scaledUnit::id);

        assertEquals(scaledUnit.id(), actual.id());
        assertEquals(scaledUnit.version(), actual.version());
    }

    @Test
    public void gettingScaledUnitsForSelectionWorks() {
        UnitDbEntity unit = standardEntities.unitDbEntity();
        ScaledUnitDbEntity scaledUnit = standardEntities.scaledUnitDbEntityBuilder().unit(unit.id()).build();
        stocksDatabase.synchronisationDao().synchroniseUnits(singletonList(unit));
        stocksDatabase.synchronisationDao().synchroniseScaledUnits(singletonList(scaledUnit));

        Observable<List<ScaledUnitForSelection>> actual = uut.getScaledUnitsForSelection();

        test(actual).assertValue(singletonList(ScaledUnitForSelection.create(scaledUnit.id(), unit.abbreviation(), scaledUnit.scale())));
    }

    @Test
    public void gettingScaledUnitsForSelectionIsSortedByScale() {
        UnitDbEntity unit = standardEntities.unitDbEntity();
        ScaledUnitDbEntity scaledUnit1 = standardEntities.scaledUnitDbEntityBuilder()
                .scale(BigDecimal.ONE)
                .unit(unit.id()).build();
        ScaledUnitDbEntity scaledUnit2 = standardEntities.scaledUnitDbEntityBuilder()
                .scale(new BigDecimal("2"))
                .unit(unit.id()).build();
        ScaledUnitDbEntity scaledUnit3 = standardEntities.scaledUnitDbEntityBuilder()
                .scale(new BigDecimal("10"))
                .unit(unit.id()).build();
        stocksDatabase.synchronisationDao().synchroniseUnits(singletonList(unit));
        stocksDatabase.synchronisationDao().synchroniseScaledUnits(List.of(scaledUnit1, scaledUnit2, scaledUnit3));

        Observable<List<ScaledUnitForSelection>> actual = uut.getScaledUnitsForSelection();

        test(actual).assertValue(List.of(
                ScaledUnitForSelection.create(scaledUnit1.id(), unit.abbreviation(), scaledUnit1.scale()),
                ScaledUnitForSelection.create(scaledUnit2.id(), unit.abbreviation(), scaledUnit2.scale()),
                ScaledUnitForSelection.create(scaledUnit3.id(), unit.abbreviation(), scaledUnit3.scale())
        ));
    }
}
