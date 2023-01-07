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

import de.njsm.stocks.client.business.entities.EanNumberForLookup;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class EanNumberRepositoryImplTest extends DbTestCase {

    private EanNumberRepositoryImpl uut;

    @Before
    public void setUp() {
        uut = new EanNumberRepositoryImpl(stocksDatabase.eanNumberDao());
    }

    @Test
    public void lookupWorks() {
        var input = standardEntities.eanNumberDbEntity();
        stocksDatabase.synchronisationDao().writeEanNumbers(List.of(input));

        var actual = uut.lookup(EanNumberForLookup.create(input.number()));

        actual.test()
                .assertNoErrors()
                .awaitCount(1)
                .assertValue(v -> input.identifies() == v.id());
    }

    @Test
    public void lookupWithoutResultCompletes() {
        var input = standardEntities.eanNumberDbEntity();
        stocksDatabase.synchronisationDao().writeEanNumbers(List.of(input));

        var actual = uut.lookup(EanNumberForLookup.create(input.number() + " other"));

        actual.test()
                .assertNoErrors()
                .assertComplete();
    }
}