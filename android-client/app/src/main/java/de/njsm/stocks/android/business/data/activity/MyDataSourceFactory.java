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
import androidx.paging.DataSource;
import androidx.paging.PositionalDataSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyDataSourceFactory extends DataSource.Factory<Key, EntityEvent<?>> {

    private List<DataSource.Factory<Integer, EntityEvent<?>>> factories;

    public MyDataSourceFactory(DataSource.Factory<Integer, EntityEvent<?>>... factories) {
        this.factories = Arrays.asList(factories);
    }

    @NonNull
    @Override
    public MyDataSource create() {
        return new MyDataSource(factories.stream()
                .map(DataSource.Factory::create)
                .map(v -> ((PositionalDataSource<EntityEvent<?>>) v))
                .map(CachingDataSource::new)
                .collect(Collectors.toList()));
    }
}
