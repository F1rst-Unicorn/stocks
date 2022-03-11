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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public abstract class InsertionTest<T extends ServerDbEntity<T>, B extends ServerDbEntity.Builder<T, B>> extends DbTestCase {

    private SynchronisationDao uut;

    abstract B getFreshDto();

    abstract List<T> getAll();

    abstract void insert(List<T> data, SynchronisationDao synchronisationDao);

    abstract void synchronise(List<T> data, SynchronisationDao synchronisationDao);

    @Before
    public void setUp() {
        uut = stocksDatabase.synchronisationDao();
    }

    @Test
    public void insertionWorks() {
        T data = getFreshDto().build();

        insert(singletonList(data), uut);

        List<T> actual = getAll();
        assertThat(actual, is(equalTo(singletonList(data))));
    }

    @Test
    public void insertionWithConflictReplaces() {
        Instant now = Instant.now();
        T dataToBeTerminated = getFreshDto().build();
        insert(singletonList(dataToBeTerminated), uut);
        dataToBeTerminated = dataToBeTerminated
                .toBuilder()
                .transactionTimeEnd(now)
                .build();
        T terminatedData = getFreshDto()
                .validTimeEnd(now)
                .build();
        List<T> input = new ArrayList<>();
        input.add(dataToBeTerminated);
        input.add(terminatedData);

        insert(input, uut);

        setArtificialDbNow(now);
        List<T> actual = getAll();
        assertThat(actual, is(equalTo(emptyList())));
    }

    @Test
    public void synchronisingWorks() {
        T data = getFreshDto().build();
        insert(singletonList(data), uut);
        data = data.toBuilder().version(data.version() + 1).build();

        synchronise(singletonList(data), uut);

        List<T> actual = getAll();
        assertThat(actual, is(equalTo(singletonList(data))));
    }
}
