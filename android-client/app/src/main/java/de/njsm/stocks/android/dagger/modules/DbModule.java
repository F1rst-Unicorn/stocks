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

package de.njsm.stocks.android.dagger.modules;

import android.app.Application;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.android.db.StocksDatabase;
import de.njsm.stocks.android.db.dao.*;
import de.njsm.stocks.android.db.migrations.Migration_31_to_32;
import de.njsm.stocks.android.db.migrations.Migration_32_to_33;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

@Module
public abstract class DbModule {

    @Provides
    @Singleton
    static StocksDatabase provideDatabase(Application context, Executor executor) {
        return Room.databaseBuilder(context, StocksDatabase.class, "stocks.db")
                .addMigrations(new Migration_31_to_32())
                .addMigrations(new Migration_32_to_33())
                .setQueryExecutor(executor)
                .setTransactionExecutor(executor)
                .build();
    }

    @Provides
    static UserDao provideUserDao(StocksDatabase database) {
        return database.userDao();
    }

    @Provides
    static LocationDao provideLocationDao(StocksDatabase database) {
        return database.locationDao();
    }

    @Provides
    static UserDeviceDao provideUserDeviceDao(StocksDatabase database) {
        return database.userDeviceDao();
    }

    @Provides
    static UpdateDao provideUpdateDao(StocksDatabase database) {
        return database.updateDao();
    }

    @Provides
    static FoodDao provideFoodDao(StocksDatabase database) {
        return database.foodDao();
    }

    @Provides
    static FoodItemDao provideFoodItemDao(StocksDatabase database) {
        return database.foodItemDao();
    }

    @Provides
    static EanNumberDao provideEanNumberDao(StocksDatabase database) {
        return database.eanNumberDao();
    }

    @Provides
    static SearchSuggestionDao provideSearchSuggestionDao(StocksDatabase database) {
        return database.searchSuggestionDao();
    }

    @Provides
    static EventDao provideEventDao(StocksDatabase database) {
        return database.eventDao();
    }

    @Provides
    static PlotDao providePlotDao(StocksDatabase database) {
        return database.plotDao();
    }

    @Provides
    static UnitDao provideUnitDao(StocksDatabase database) {
        return database.unitDao();
    }
}
