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
import androidx.paging.ItemKeyedDataSource;

import java.util.ArrayList;
import java.util.List;

import de.njsm.stocks.android.business.data.activity.EntityEvent;
import de.njsm.stocks.android.util.Logger;

import static de.njsm.stocks.android.util.paging.CachingDataSource.Direction.BACKWARD;
import static de.njsm.stocks.android.util.paging.CachingDataSource.Direction.FORWARD;

public class MergingDataSource extends ItemKeyedDataSource<Key, EntityEvent<?>> {

    private static final Logger LOG = new Logger(MergingDataSource.class);

    private List<CachingDataSource> sources;

    public MergingDataSource(List<CachingDataSource> sources) {
        this.sources = sources;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Key> params, @NonNull LoadInitialCallback<EntityEvent<?>> callback) {
        LOG.d("Loading initial " + params.requestedLoadSize + " elements");

        Key position = new Key(sources.size());

        List<EntityEvent<?>> result = new ArrayList<>();

        int totalCount = sources.stream().map(CachingDataSource::count).reduce(0, Integer::sum);

        while (result.size() < params.requestedLoadSize) {
            int i = 0;
            int minIndex = -1;
            EntityEvent<?> min = null;
            for (CachingDataSource item : sources) {
                EntityEvent<?> current = item.get(position.getIndex(i), params.requestedLoadSize, FORWARD);
                if (current == null) {
                    i++;
                    continue;
                }
                if (min == null || min.compareTo(current) > 0) {
                    min = current;
                    minIndex = i;
                }
                i++;
            }

            if (min == null) {
                break;
            }
            LOG.d("Yield " + minIndex);

            position.setKeyIndex(minIndex);
            min.setKey(position.copy());
            result.add(min);

            position.increment(minIndex);
        }

        callback.onResult(result, 0, totalCount);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Key> params, @NonNull LoadCallback<EntityEvent<?>> callback) {
        Key position = params.key.copy();

        LOG.d("Loading after " + position.getPosition() + " (" + position.getKeyIndex() + "), " + params.requestedLoadSize + " elements");

        EntityEvent<?> pivot = sources.get(position.getKeyIndex()).get(position.getPartialPosition(), params.requestedLoadSize, FORWARD);

        position.increment(position.getKeyIndex());

        List<EntityEvent<?>> result = new ArrayList<>();

        while (result.size() < params.requestedLoadSize) {
            int i = 0;
            int minIndex = -1;
            EntityEvent<?> min = null;
            for (CachingDataSource item : sources) {
                EntityEvent<?> current = item.get(position.getIndex(i), params.requestedLoadSize, FORWARD);
                if (current == null) {
                    i++;
                    continue;
                }
                if (pivot.compareTo(current) < 0 && (min == null || min.compareTo(current) > 0)) {
                    min = current;
                    minIndex = i;
                }
                i++;
            }

            if (min == null) {
                break;
            }

            LOG.d("Yield " + minIndex + " timestamp " + min.getTime());

            position.setKeyIndex(minIndex);
            min.setKey(position.copy());
            result.add(min);

            position.increment(minIndex);
        }

        callback.onResult(result);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Key> params, @NonNull LoadCallback<EntityEvent<?>> callback) {
        Key position = params.key.copy();

        LOG.d("Loading before " + position.getPosition() + " (" + position.getKeyIndex() + "), " + params.requestedLoadSize + " elements");

        EntityEvent<?> pivot = sources.get(position.getKeyIndex()).get(position.getPartialPosition(), params.requestedLoadSize, BACKWARD);

        position.decrement(position.getKeyIndex());

        List<EntityEvent<?>> result = new ArrayList<>();

        while (result.size() < params.requestedLoadSize) {
            int i = 0;
            int maxIndex = -1;
            EntityEvent<?> max = null;
            for (CachingDataSource item : sources) {
                EntityEvent<?> current = item.get(position.getIndex(i), params.requestedLoadSize, BACKWARD);
                if (current == null) {
                    i++;
                    continue;
                }
                if (pivot.compareTo(current) > 0 && (max == null || max.compareTo(current) < 0)) {
                    max = current;
                    maxIndex = i;
                }
                i++;
            }

            if (max == null) {
                break;
            }

            LOG.d("Yield " + maxIndex);

            position.setKeyIndex(maxIndex);
            max.setKey(position.copy());
            result.add(max);

            position.decrement(maxIndex);
        }

        callback.onResult(result);
    }

    @NonNull
    @Override
    public Key getKey(@NonNull EntityEvent<?> item) {
        return item.getKey();
    }
}
