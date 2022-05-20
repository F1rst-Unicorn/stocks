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

import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.njsm.stocks.client.database.StandardEntities.unitDbEntity;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UnitRepositoryImplTest extends DbTestCase {

    private UnitRepositoryImpl uut;

    @Before
    public void setUp() {
        uut = new UnitRepositoryImpl(stocksDatabase.unitDao());
    }

    @Test
    public void gettingUnitsWorks() {
        List<UnitDbEntity> entities = singletonList(unitDbEntity());
        stocksDatabase.synchronisationDao().synchroniseUnits(entities);
        List<UnitForListing> expected = entities.stream().map(DataMapper::map).collect(toList());

        Observable<List<UnitForListing>> actual = uut.getUnits();

        actual.test().awaitCount(1).assertValue(expected);
    }

    @Test
    public void gettingSingleUnitWorks() {
        UnitDbEntity entity = unitDbEntity();
        List<UnitDbEntity> entities = singletonList(entity);
        stocksDatabase.synchronisationDao().synchroniseUnits(entities);
        UnitToEdit expected = UnitToEdit.create(entity.id(), entity.name(), entity.abbreviation());

        Observable<UnitToEdit> actual = uut.getUnit(entity::id);

        actual.test().awaitCount(1).assertValue(expected);
    }

    @Test
    public void gettingUnitForDeletionWorks() {
        UnitDbEntity unit = unitDbEntity();
        stocksDatabase.synchronisationDao().synchroniseUnits(singletonList(unit));

        UnitForDeletion actual = uut.getEntityForDeletion(unit::id);

        assertEquals(unit.id(), actual.id());
        assertEquals(unit.version(), actual.version());
    }

    @Test
    public void gettingUnitInBackgroundForEditingWorks() {
        UnitDbEntity entity = unitDbEntity();
        stocksDatabase.synchronisationDao().synchroniseUnits(singletonList(entity));
        UnitToEdit expected = UnitToEdit.create(entity.id(), entity.name(), entity.abbreviation());

        UnitForEditing actual = uut.getCurrentDataBeforeEditing(expected);

        assertTrue(expected.isContainedIn(actual));
        assertEquals(entity.version(), actual.version());
    }

    @Test
    public void gettingUnitForSelectionWorks() {
        UnitDbEntity entity = unitDbEntity();
        stocksDatabase.synchronisationDao().synchroniseUnits(singletonList(entity));

        Observable<List<UnitForSelection>> actual = uut.getUnitsForSelection();

        actual.test().awaitCount(1).assertValue(v -> v.get(0).id() == entity.id()
                && v.get(0).name().equals(entity.name()));
    }
}
