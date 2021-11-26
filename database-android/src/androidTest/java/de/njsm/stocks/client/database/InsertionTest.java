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
public abstract class InsertionTest<T extends DbEntity> extends DbTestCase {

    abstract Inserter<T> getDao();

    abstract T getDto();

    abstract void alterDto(T data);

    @Test
    public void insertionWorks() {
        T data = getDto();

        getDao().insert(singletonList(data));

        List<T> actual = getDao().getAll();
        assertThat(actual, is(equalTo(singletonList(data))));
    }

    @Test
    public void insertionWithConflictReplaces() {
        Instant now = Instant.now();
        T dataToBeTerminated = getDto();
        getDao().insert(singletonList(dataToBeTerminated));
        dataToBeTerminated.setTransactionTimeEnd(now);
        T terminatedData = getDto();
        terminatedData.setValidTimeEnd(now);
        List<T> input = new ArrayList<>();
        input.add(dataToBeTerminated);
        input.add(terminatedData);

        getDao().insert(input);

        setArtificialDbNow(now);
        List<T> actual = getDao().getAll();
        assertThat(actual, is(equalTo(emptyList())));
    }

    @Test
    public void synchronisingWorks() {
        T data = getDto();
        getDao().insert(singletonList(data));
        alterDto(data);

        getDao().synchronise(singletonList(data));

        List<T> actual = getDao().getAll();
        assertThat(actual, is(equalTo(singletonList(data))));
    }
}
