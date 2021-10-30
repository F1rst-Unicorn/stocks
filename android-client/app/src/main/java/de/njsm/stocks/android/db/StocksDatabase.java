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

package de.njsm.stocks.android.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import de.njsm.stocks.android.db.dao.*;
import de.njsm.stocks.android.db.dbview.*;
import de.njsm.stocks.android.db.entities.*;

@Database(
        entities = {
                User.class,
                UserDevice.class,
                Update.class,
                Location.class,
                Food.class,
                FoodItem.class,
                EanNumber.class,
                SearchSuggestion.class,
                Unit.class,
                ScaledUnit.class,
                Recipe.class,
                RecipeIngredient.class,
                RecipeProduct.class,
        },
        views = {
                CurrentUser.class,
                CurrentUserDevice.class,
                CurrentLocation.class,
                CurrentFood.class,
                CurrentFoodItem.class,
                CurrentEanNumber.class,
                CurrentUnit.class,
                CurrentScaledUnit.class,
                CurrentRecipeIngredient.class,
                CurrentRecipeProduct.class,
                CurrentRecipe.class,
                ScaledAmount.class,
                ScaledUnitConversion.class,
                RecipeIngredientAmountAndStock.class,
                RecipeStockRatingBase.class,
        },
        version = 40)
@TypeConverters(de.njsm.stocks.android.db.TypeConverters.class)
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

    public abstract UserDao userDao();

    public abstract UserDeviceDao userDeviceDao();

    public abstract LocationDao locationDao();

    public abstract UpdateDao updateDao();

    public abstract FoodDao foodDao();

    public abstract FoodItemDao foodItemDao();

    public abstract EanNumberDao eanNumberDao();

    public abstract SearchSuggestionDao searchSuggestionDao();

    public abstract EventDao eventDao();

    public abstract PlotDao plotDao();

    public abstract UnitDao unitDao();

    public abstract ScaledUnitDao scaledUnitDao();

    public abstract RecipeDao recipeDao();

    public abstract RecipeIngredientDao recipeIngredientDao();

    public abstract RecipeProductDao recipeProductDao();

    public abstract MetadataDao metadataDao();
}
