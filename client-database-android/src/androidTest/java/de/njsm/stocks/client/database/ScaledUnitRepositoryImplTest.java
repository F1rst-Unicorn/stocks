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
import de.njsm.stocks.client.business.entities.ScaledUnitForListing;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.njsm.stocks.client.database.StandardEntities.scaledUnitDbEntityBuilder;
import static de.njsm.stocks.client.database.StandardEntities.unitDbEntity;
import static java.util.Collections.singletonList;

public class ScaledUnitRepositoryImplTest extends DbTestCase {

    private ScaledUnitRepository uut;

    @Before
    public void setUp() {
        uut = new ScaledUnitRepositoryImpl(stocksDatabase.scaledUnitDao());
    }

    @Test
    public void gettingScaledUnitsWorks() {
        UnitDbEntity unit = unitDbEntity();
        ScaledUnitDbEntity scaledUnit = scaledUnitDbEntityBuilder().unit(unit.id()).build();
        stocksDatabase.synchronisationDao().synchroniseUnits(singletonList(unit));
        stocksDatabase.synchronisationDao().synchroniseScaledUnits(singletonList(scaledUnit));

        Observable<List<ScaledUnitForListing>> actual = uut.getScaledUnits();

        actual.test().awaitCount(1).assertValue(singletonList(ScaledUnitForListing.create(scaledUnit.id(), unit.abbreviation(), scaledUnit.scale())));
    }
}
