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
 */

package de.njsm.stocks.client.database;

import android.app.Application;
import androidx.room.Room;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.client.business.LocationRepository;
import de.njsm.stocks.client.business.SynchronisationRepository;
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

@Module
public abstract class DatabaseModule {

    @Provides
    @Singleton
    static StocksDatabase provideDatabase(Application context, Executor executor) {
        return Room.databaseBuilder(context, StocksDatabase.class, "stocks.db")
                .setQueryExecutor(executor)
                .setTransactionExecutor(executor)
                .fallbackToDestructiveMigration()
                .openHelperFactory(new RequerySQLiteOpenHelperFactory())
                .build();
    }

    @Binds
    public abstract LocationRepository locationRepository(LocationRepositoryImpl impl);

    @Binds
    public abstract SynchronisationRepository synchronisationRepository(SynchronisationRepositoryImpl impl);

    @Provides
    static LocationDao provideUserDao(StocksDatabase database) {
        return database.locationDao();
    }

    @Provides
    static SynchronisationDao synchronisationDao(StocksDatabase database) {
        return database.synchronisationDao();
    }

    @Provides
    static MetadataDao metadataDao(StocksDatabase database) {
        return database.metadataDao();
    }
}
