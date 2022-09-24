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
import de.njsm.stocks.client.business.*;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.database.error.ConflictRepositoryImpl;
import de.njsm.stocks.client.database.error.ErrorDao;
import de.njsm.stocks.client.database.error.ErrorRecorderImpl;
import de.njsm.stocks.client.database.error.ErrorRepositoryImpl;
import de.njsm.stocks.client.execution.Scheduler;
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

@Module
public interface DatabaseModule {

    Logger LOG = LoggerFactory.getLogger(DatabaseModule.class);

    @Provides
    @Singleton
    static StocksDatabase provideDatabase(Application context, Scheduler scheduler) {
        Executor executor = toExecutor(scheduler);
        return Room.databaseBuilder(context, StocksDatabase.class, "stocks.db")
                .setQueryExecutor(executor)
                .setTransactionExecutor(executor)
                .fallbackToDestructiveMigration()
                .openHelperFactory(new RequerySQLiteOpenHelperFactory())
                .setQueryCallback((sqlQuery, bindArgs) -> {
                    if (bindArgs.isEmpty())
                        LOG.trace(sqlQuery);
                    else
                        LOG.trace(sqlQuery + "; args " + bindArgs);
                }, executor)
                .build();
    }

    static Executor toExecutor(Scheduler scheduler) {
        return command -> scheduler.schedule(Job.create(Job.Type.DATABASE, command));
    }

    @Binds
    LocationRepository locationRepository(LocationRepositoryImpl impl);

    @Binds
    EntityDeleteRepository<Location> locationDeleterRepository(LocationRepository impl);

    @Binds
    SynchronisationRepository synchronisationRepository(SynchronisationRepositoryImpl impl);

    @Binds
    ErrorRecorder errorRecorder(ErrorRecorderImpl impl);

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

    @Provides
    static ErrorDao errorDao(StocksDatabase database) {
        return database.errorDao();
    }

    @Provides
    static UnitDao unitDao(StocksDatabase database) {
        return database.unitDao();
    }

    @Provides
    static ScaledUnitDao ScaledUnitDao(StocksDatabase database) {
        return database.scaledUnitDao();
    }

    @Provides
    static FoodDao FoodDao(StocksDatabase database) {
        return database.foodDao();
    }

    @Binds
    ErrorRepository errorRepository(ErrorRepositoryImpl impl);

    @Binds
    ConflictRepository conflictRepository(ConflictRepositoryImpl impl);

    @Binds
    UnitRepository unitRepository(UnitRepositoryImpl impl);

    @Binds
    EntityDeleteRepository<Unit> unitDeleterRepository(UnitRepository impl);

    @Binds
    ScaledUnitRepository ScaledUnitRepository(ScaledUnitRepositoryImpl impl);

    @Binds
    ScaledUnitAddRepository ScaledUnitAddRepository(UnitRepositoryImpl impl);

    @Binds
    ScaledUnitEditRepository ScaledUnitEditRepository(ScaledUnitEditRepositoryImpl impl);

    @Binds
    EntityDeleteRepository<ScaledUnit> ScaledUnitDeleteRepository(ScaledUnitRepositoryImpl impl);

    @Binds
    FoodAddRepository FoodAddRepository(FoodAddRepositoryImpl impl);

    @Binds
    EmptyFoodRepository EmptyFoodRepository(FoodRepositoryImpl impl);

    @Binds
    EntityDeleteRepository<Food> FoodDeleteRepository(FoodRepositoryImpl impl);

    @Binds
    FoodEditRepository FoodEditRepository(FoodEditRepositoryImpl impl);

    @Binds
    FoodListRepository FoodListRepository(FoodListRepositoryImpl impl);
}
