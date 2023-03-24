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

package de.njsm.stocks.client.database;

import androidx.room.Database;
import androidx.room.RewriteQueriesToDropUnusedColumns;
import androidx.room.RoomDatabase;
import de.njsm.stocks.client.database.error.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Database(
        entities = {
                UpdateDbEntity.class,
                LocationDbEntity.class,
                StatusCodeExceptionEntity.class,
                SubsystemExceptionEntity.class,
                LocationAddEntity.class,
                ErrorEntity.class,
                LocationDeleteEntity.class,
                LocationEditEntity.class,
                UserDbEntity.class,
                UserDeviceDbEntity.class,
                FoodDbEntity.class,
                EanNumberDbEntity.class,
                FoodItemDbEntity.class,
                UnitDbEntity.class,
                ScaledUnitDbEntity.class,
                RecipeDbEntity.class,
                RecipeIngredientDbEntity.class,
                RecipeProductDbEntity.class,
                UnitAddEntity.class,
                UnitDeleteEntity.class,
                UnitEditEntity.class,
                ScaledUnitAddEntity.class,
                ScaledUnitEditEntity.class,
                ScaledUnitDeleteEntity.class,
                FoodAddEntity.class,
                FoodDeleteEntity.class,
                FoodEditEntity.class,
                FoodItemAddEntity.class,
                FoodItemDeleteEntity.class,
                FoodItemEditEntity.class,
                EanNumberAddEntity.class,
                EanNumberDeleteEntity.class,
                UserDeviceDeleteEntity.class,
                UserDeleteEntity.class,
                RecipeAddEntity.class,
                RecipeIngredientAddEntity.class,
                RecipeProductAddEntity.class,
                RecentSearchDbEntity.class,
                SearchedFoodDbEntity.class,
                FoodToBuyEntity.class,
                UserAddEntity.class,
                UserDeviceAddEntity.class,
                TicketEntity.class,
        },
        views = {
                CurrentLocationDbView.class,
                CurrentUserDbView.class,
                CurrentUserDeviceDbView.class,
                CurrentFoodDbView.class,
                CurrentEanNumberDbView.class,
                CurrentFoodItemDbView.class,
                CurrentUnitDbView.class,
                CurrentScaledUnitDbView.class,
                CurrentRecipeDbView.class,
                CurrentRecipeIngredientDbView.class,
                CurrentRecipeProductDbView.class,
        },
        version = 48)
@androidx.room.TypeConverters(TypeConverters.class)
@RewriteQueriesToDropUnusedColumns
public abstract class StocksDatabase extends RoomDatabase {

    /**
     * <code>datetime('now')</code> is precise up to second. Server time is
     * measured in microseconds. If <code>last_update</code> contains a larger
     * value than <code>datetime('now')</code> it is closer to the present than
     * <code>datetime('now')</code>.
     *
     * This solution prevents entities to be presented to the user which will be
     * absent on the server in the same second as the one reported by <code>datetime('now')</code>.
     */
    public static final String NOW = "(select max(x) from (select datetime('now') as x union select max(last_update) as x from updates)) ";

    static final DateTimeFormatter DATABASE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS").withZone(ZoneId.of("UTC"));

    public static final String DATABASE_INFINITY_STRING = "9999-12-31 23:59:59.999999";

    public static final String DATABASE_INFINITY_STRING_SQL = "'" + DATABASE_INFINITY_STRING + "'";

    static final Instant DATABASE_INFINITY = DATABASE_DATE_FORMAT.parse(DATABASE_INFINITY_STRING, Instant::from);

    abstract LocationDao locationDao();

    public abstract SynchronisationDao synchronisationDao();

    abstract MetadataDao metadataDao();

    public abstract ErrorDao errorDao();

    abstract UserDao userDao();

    abstract UserDeviceDao userDeviceDao();

    abstract FoodDao foodDao();

    abstract EanNumberDao eanNumberDao();

    abstract FoodItemDao foodItemDao();

    abstract UnitDao unitDao();

    abstract ScaledUnitDao scaledUnitDao();

    abstract RecipeDao recipeDao();

    abstract RecipeIngredientDao recipeIngredientDao();

    abstract RecipeProductDao recipeProductDao();

    abstract SearchDao searchDao();

    abstract EventDao eventDao();
}
