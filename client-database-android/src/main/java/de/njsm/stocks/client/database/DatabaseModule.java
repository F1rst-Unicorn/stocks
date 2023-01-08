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
import dagger.android.ContributesAndroidInjector;
import de.njsm.stocks.client.business.*;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.business.event.EventRepository;
import de.njsm.stocks.client.database.contentprovider.SearchSuggestionsProvider;
import de.njsm.stocks.client.database.error.ConflictRepositoryImpl;
import de.njsm.stocks.client.database.error.ErrorDao;
import de.njsm.stocks.client.database.error.ErrorRecorderImpl;
import de.njsm.stocks.client.database.error.ErrorRepositoryImpl;
import de.njsm.stocks.client.database.migration.Legacy40To44;
import de.njsm.stocks.client.database.migration.Migration44To45;
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
                .openHelperFactory(new RequerySQLiteOpenHelperFactory())
                .setQueryCallback((sqlQuery, bindArgs) -> {
                    if (!LOG.isTraceEnabled())
                        return;

                    if (bindArgs.isEmpty())
                        LOG.trace(sqlQuery);
                    else
                        LOG.trace(sqlQuery + "; args " + bindArgs);
                }, executor)
                .addMigrations(new Legacy40To44(),
                        new Migration44To45())
                .addCallback(new PerformanceTweaker())
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

    @Provides
    static FoodItemDao FoodItemDao(StocksDatabase database) {
        return database.foodItemDao();
    }

    @Provides
    static UserDao UserDao(StocksDatabase database) {
        return database.userDao();
    }

    @Provides
    static UserDeviceDao UserDeviceDao(StocksDatabase database) {
        return database.userDeviceDao();
    }

    @Provides
    static RecipeDao RecipeDao(StocksDatabase database) {
        return database.recipeDao();
    }

    @Provides
    static EanNumberDao EanNumberDao(StocksDatabase database) {
        return database.eanNumberDao();
    }

    @Provides
    static SearchDao SearchDao(StocksDatabase database) {
        return database.searchDao();
    }

    @Provides
    static EventDao EventDao(StocksDatabase database) {
        return database.eventDao();
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

    @Binds
    FoodItemListRepository FoodItemListRepository(FoodItemListRepositoryImpl impl);

    @Binds
    FoodItemAddRepository FoodItemAddRepository(FoodItemAddRepositoryImpl impl);

    @Binds
    EntityDeleteRepository<FoodItem> FoodItemListRepositoryImpl(FoodItemListRepositoryImpl impl);

    @Binds
    FoodItemEditRepository FoodItemEditRepository(FoodItemEditRepositoryImpl impl);

    @Binds
    UserListRepository UserListRepository(UserListRepositoryImpl impl);

    @Binds
    UserDeviceListRepository UserDeviceListRepository(UserDeviceListRepositoryImpl impl);

    @Binds
    RecipeListRepository RecipeListRepository(RecipeListRepositoryImpl impl);

    @Binds
    EanNumberListRepository EanNumberListRepository(EanNumberListRepositoryImpl impl);

    @Binds
    EntityDeleteRepository<EanNumber> EanNumberDeleteRepository(EanNumberListRepositoryImpl impl);

    @Binds
    EntityDeleteRepository<UserDevice> UserDeviceDeleteRepository(UserDeviceListRepositoryImpl impl);

    @Binds
    EntityDeleteRepository<User> UserDeleteRepository(UserListRepositoryImpl impl);

    @Binds
    RecipeAddRepository RecipeAddRepository(RecipeAddRepositoryImpl impl);

    @Binds
    SearchRepository SearchRepository(SearchRepositoryImpl impl);

    @Binds
    FoodToBuyRepository FoodToBuyRepository(FoodToBuyRepositoryImpl impl);

    @Binds
    EventRepository EventRepository(EventRepositoryImpl impl);

    @ContributesAndroidInjector
    SearchSuggestionsProvider SearchSuggestionsProvider();

    @Binds
    EanNumberLookupRepository EanNumberLookupRepository(EanNumberRepositoryImpl impl);
}
