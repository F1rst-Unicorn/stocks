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

import de.njsm.stocks.client.business.entities.EanNumberForListing;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.njsm.stocks.client.database.util.Util.testList;

public class EanNumberListRepositoryImplTest extends DbTestCase {

    private EanNumberListRepositoryImpl uut;

    @Before
    public void setUp() {
        uut = new EanNumberListRepositoryImpl(stocksDatabase.eanNumberDao());
    }

    @Test
    public void gettingUsersWorks() {
        EanNumberDbEntity eanNumber = standardEntities.eanNumberDbEntity();
        stocksDatabase.synchronisationDao().writeEanNumbers(List.of(eanNumber));

        var actual = uut.get(eanNumber::identifies);

        testList(actual).assertValue(List.of(EanNumberForListing.create(eanNumber.id(), eanNumber.number())));
    }
}