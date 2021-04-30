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

package de.njsm.stocks.android.util.paging;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PositionalDataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import de.njsm.stocks.android.business.data.activity.EntityEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CachingDataSourceTest {

    private CachingDataSource uut;

    @Mock
    private PositionalDataSource<EntityEvent<?>> source;

    @Before
    public void setup() {
        uut = new CachingDataSource(source);
    }



    @Test
    public void initialCountingLoadsFromSource() {
        int expected = 14;
        source = new PositionalDataSource<EntityEvent<?>>() {

            @Override
            public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback callback) {
                callback.onResult(Collections.emptyList(), 0, expected);
            }

            @Override
            public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback callback) {
            }
        };
        uut = new CachingDataSource(source);

        int actual = uut.count();

        assertEquals(expected, actual);
    }

    @Test
    public void secondCountingReturnsDirectly() {
        int expected = 14;
        source = new PositionalDataSource<EntityEvent<?>>() {
            boolean called = false;
            @Override
            public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback callback) {
                if (called) {
                    throw new RuntimeException("Expected to be called only once");
                }
                called = true;
                callback.onResult(Collections.emptyList(), 0, expected);
            }

            @Override
            public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback callback) {
            }
        };
        uut = new CachingDataSource(source);

        int actual = uut.count();
        assertEquals(expected, actual);

        actual = uut.count();
        assertEquals(expected, actual);
    }

    @Test
    public void leftEndIsOfBackwardIsComputed() {
        assertEquals(51, CachingDataSource.Direction.BACKWARD.getLeftEnd(100, 50));
        assertEquals(52, CachingDataSource.Direction.BACKWARD.getLeftEnd(101, 50));
        assertEquals(53, CachingDataSource.Direction.BACKWARD.getLeftEnd(101, 49));
    }

    @Test
    public void leftEndIsIdentityForForward() {
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                assertEquals(i, CachingDataSource.Direction.FORWARD.getLeftEnd(i, j));
    }

    @Test
    public void invalidQueryIsForwarded() {
        Mockito.when(source.isInvalid()).thenReturn(true);

        boolean output = uut.isInvalid();

        assertTrue(output);
        Mockito.verify(source).isInvalid();
    }

    @Test
    public void callbackIsForwarded() {
        DataSource.InvalidatedCallback callback = Mockito.mock(DataSource.InvalidatedCallback.class);

        uut.addInvalidatedCallback(callback);

        Mockito.verify(source).addInvalidatedCallback(callback);
    }
}