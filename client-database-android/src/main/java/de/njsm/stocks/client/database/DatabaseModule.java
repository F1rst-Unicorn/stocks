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
import de.njsm.stocks.client.business.entities.Job;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.entities.ScaledUnit;
import de.njsm.stocks.client.business.entities.Unit;
import de.njsm.stocks.client.database.error.ConflictRepositoryImpl;
import de.njsm.stocks.client.database.error.ErrorDao;
import de.njsm.stocks.client.database.error.ErrorRecorderImpl;
import de.njsm.stocks.client.database.error.ErrorRepositoryImpl;
import de.njsm.stocks.client.execution.Scheduler;
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory;

import javax.inject.Singleton;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Module
public interface DatabaseModule {

    @Provides
    @Singleton
    static StocksDatabase provideDatabase(Application context, Scheduler scheduler) {
        return Room.databaseBuilder(context, StocksDatabase.class, "stocks.db")
                .setQueryExecutor(toExecutor(scheduler))
                .setTransactionExecutor(toExecutor(scheduler))
                .fallbackToDestructiveMigration()
                .openHelperFactory(new RequerySQLiteOpenHelperFactory())
                .build();
    }

    static Executor toExecutor(Scheduler scheduler) {
        return command -> scheduler.schedule(Job.create(Job.Type.DATABASE, command));
    }

    @Provides
    static Supplier<Instant> clock() {
        return Instant::now;
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
}
