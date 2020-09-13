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

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.paging.DataSource;
import androidx.paging.PositionalDataSource;

import java.util.Collections;
import java.util.List;

import de.njsm.stocks.android.business.data.activity.EntityEvent;

public class CachingDataSource {

    private PositionalDataSource<EntityEvent<?>> source;

    private int cursor;

    private List<EntityEvent<?>> cache;

    private LoadRangeCallback callback;

    private int count;

    public CachingDataSource(PositionalDataSource<EntityEvent<?>> source) {
        this.source = source;
        this.cursor = -1;
        this.cache = Collections.emptyList();
        this.callback = new LoadRangeCallback();
        this.count = -1;
    }

    public EntityEvent<?> get(int position, int pageSize, Direction direction) {
        if (cursor > position || position > lastPosition()) {
            load(Math.max(position, 0), pageSize, direction);
        }

        if (cursor > position || position > lastPosition()) {
            return null;
        }

        return cache.get(position - cursor);
    }

    public int count() {
        if (count == -1) {
            PositionalDataSource.LoadInitialParams params = new PositionalDataSource.LoadInitialParams(0, 1, 1, true);
            source.loadInitial(params, new LoadInitialCallback());
        }
        return count;
    }

    private void load(int position, int pageSize, Direction direction) {
        if (position == cursor)
            return;

        position = direction.getLeftEnd(position, pageSize);
        if (position >= count())
            return;

        PositionalDataSource.LoadRangeParams params = new PositionalDataSource.LoadRangeParams(position, pageSize);
        source.loadRange(params, callback);

        if (size() > 0)
            cursor = position;
    }

    private int size() {
        return cache.size();
    }

    private int lastPosition() {
        return cursor + cache.size() - 1;
    }

    private class LoadRangeCallback extends PositionalDataSource.LoadRangeCallback<EntityEvent<?>> {

        @Override
        public void onResult(@NonNull List<EntityEvent<?>> data) {
            CachingDataSource.this.cache = data;
        }
    }

    private class LoadInitialCallback extends PositionalDataSource.LoadInitialCallback<EntityEvent<?>> {

        @Override
        public void onResult(@NonNull List<EntityEvent<?>> data, int position, int totalCount) {
            CachingDataSource.this.count = totalCount;
        }

        @Override
        public void onResult(@NonNull List<EntityEvent<?>> data, int position) {
            throw new UnsupportedOperationException("Call the other method");
        }
    }

    enum Direction {
        FORWARD {
            @Override
            public int getLeftEnd(int position, int pageSize) {
                return position;
            }
        },
        BACKWARD {
            @Override
            public int getLeftEnd(int position, int pageSize) {
                return position - pageSize + 1;
            }
        },
        ;

        public abstract int getLeftEnd(int position, int pageSize);
    }

    @AnyThread
    public void addInvalidatedCallback(@NonNull DataSource.InvalidatedCallback onInvalidatedCallback) {
        source.addInvalidatedCallback(onInvalidatedCallback);
    }

    @WorkerThread
    public boolean isInvalid() {
        return source.isInvalid();
    }
}
