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

import de.njsm.stocks.client.business.UnitRepository;
import de.njsm.stocks.client.business.entities.UnitForListing;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static de.njsm.stocks.client.database.StandardEntities.unitDbEntity;
import static java.util.stream.Collectors.toList;

public class UnitRepositoryImplTest extends DbTestCase {

    private UnitRepository uut;

    @Before
    public void setUp() {
        uut = new UnitRepositoryImpl(stocksDatabase.unitDao());
    }

    @Test
    public void gettingUnitsWorks() {
        List<UnitDbEntity> entities = Collections.singletonList(unitDbEntity());
        stocksDatabase.synchronisationDao().synchroniseUnits(entities);
        List<UnitForListing> expected = entities.stream().map(DataMapper::map).collect(toList());

        Observable<List<UnitForListing>> actual = uut.getUnits();

        actual.test().awaitCount(1).assertValue(expected);
    }
}
