/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.db;

import androidx.lifecycle.LiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import de.njsm.stocks.android.db.dao.Inserter;
import de.njsm.stocks.android.db.entities.VersionedData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.Instant;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public abstract class InsertionTest<T extends VersionedData> extends DbTestCase {

    abstract Inserter<T> getDao();

    abstract T getDto();

    abstract void alterDto(T data);

    abstract T[] toArray(T data);

    abstract T[] toArray(T data, T data2);

    @Test
    public void insertionWorks() {
        T data = getDto();

        getDao().insert(toArray(data));

        LiveData<List<T>> actual = getDao().getAll();
        actual.observeForever(v -> assertEquals(Collections.singletonList(data), v));
    }

    @Test
    public void insertionWithConflictReplaces() {
        Instant now = Instant.now();
        T dataToBeTerminated = getDto();
        getDao().insert(toArray(dataToBeTerminated));
        dataToBeTerminated.transactionTimeEnd = now;
        T terminatedData = getDto();
        terminatedData.validTimeEnd = now;

        getDao().insert(toArray(dataToBeTerminated, terminatedData));

        setArtificialDbNow(now);
        LiveData<List<T>> actual = getDao().getAll();
        actual.observeForever(v -> assertEquals(Collections.emptyList(), v));
    }

    @Test
    public void synchronisingWorks() {
        T data = getDto();
        getDao().insert(toArray(data));
        alterDto(data);

        getDao().synchronise(toArray(data));

        LiveData<List<T>> actual = getDao().getAll();
        actual.observeForever(v -> assertEquals(Collections.singletonList(data), v));
    }
}
