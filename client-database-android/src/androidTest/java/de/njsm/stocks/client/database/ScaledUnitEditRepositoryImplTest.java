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

import de.njsm.stocks.client.business.ScaledUnitEditRepository;
import de.njsm.stocks.client.business.entities.ScaledUnitForEditing;
import de.njsm.stocks.client.business.entities.ScaledUnitToEdit;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import static de.njsm.stocks.client.database.util.Util.test;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class ScaledUnitEditRepositoryImplTest extends DbTestCase {

    private ScaledUnitEditRepository uut;

    @Before
    public void setUp() {
        uut = new ScaledUnitEditRepositoryImpl(null, stocksDatabase.scaledUnitDao());
    }

    @Test
    public void gettingScaledUnitWorks() {
        UnitDbEntity unit = standardEntities.unitDbEntity();
        ScaledUnitDbEntity scaledUnit = standardEntities.scaledUnitDbEntityBuilder().unit(unit.id()).build();
        stocksDatabase.synchronisationDao().synchroniseUnits(singletonList(unit));
        stocksDatabase.synchronisationDao().synchroniseScaledUnits(singletonList(scaledUnit));

        Observable<ScaledUnitToEdit> actual = uut.getScaledUnit(scaledUnit::id);

        test(actual).assertValue(ScaledUnitToEdit.create(scaledUnit.id(), scaledUnit.scale(), scaledUnit.unit()));
    }

    @Test
    public void gettingScaledUnitWithVersionWorks() {
        UnitDbEntity unit = standardEntities.unitDbEntity();
        ScaledUnitDbEntity scaledUnit = standardEntities.scaledUnitDbEntityBuilder().unit(unit.id()).build();
        stocksDatabase.synchronisationDao().synchroniseUnits(singletonList(unit));
        stocksDatabase.synchronisationDao().synchroniseScaledUnits(singletonList(scaledUnit));

        ScaledUnitForEditing actual = uut.getScaledUnitForSending(scaledUnit::id);

        ScaledUnitForEditing expected = ScaledUnitForEditing.create(scaledUnit.id(), scaledUnit.version(), scaledUnit.scale(), scaledUnit.unit());
        assertEquals(expected, actual);
    }
}
