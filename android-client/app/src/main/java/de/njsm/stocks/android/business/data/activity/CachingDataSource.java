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

package de.njsm.stocks.android.business.data.activity;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import java.util.Collections;
import java.util.List;

import de.njsm.stocks.android.util.Logger;

public class CachingDataSource {

    private static final int PAGE_SIZE = 7;

    private static final Logger LOG = new Logger(CachingDataSource.class);

    private PositionalDataSource<EntityEvent<?>> source;

    private int cursor;

    private List<EntityEvent<?>> cache;

    private LoadRangeCallback callback;

    public CachingDataSource(PositionalDataSource<EntityEvent<?>> source) {
        this.source = source;
        this.cursor = -1;
        this.cache = Collections.emptyList();
        this.callback = new LoadRangeCallback();
    }

    public EntityEvent<?> get(int position) {
        LOG.d("At [" + cursor + ", " + lastPosition() + "], queried for " + position);

        if (cursor > position || position > lastPosition()) {
            load(Math.max(position - PAGE_SIZE / 2, 0));
        }

        if (cursor > position || position > lastPosition()) {
            LOG.d("no result");
            return null;
        }

        return cache.get(position - cursor);
    }

    private void load(int position) {
        if (position == cursor)
            return;

        PositionalDataSource.LoadRangeParams params = new PositionalDataSource.LoadRangeParams(position, PAGE_SIZE);
        source.loadRange(params, callback);

        if (size() > 0)
            cursor = position;

        LOG.d("Moved to [" + cursor + ", " + lastPosition() + "]");
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
}
