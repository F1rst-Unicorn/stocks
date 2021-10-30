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

package de.njsm.stocks.android.db.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import de.njsm.stocks.android.db.dbview.*;

import java.util.Arrays;
import java.util.List;

public class Migration_39_to_40 extends Migration {

    public Migration_39_to_40() {
        super(39, 40);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        List<String> tables = Arrays.asList(
                "user",
                "user_device",
                "location",
                "food",
                "fooditem",
                "eannumber",
                "unit",
                "scaled_unit",
                "recipe",
                "recipe_ingredient",
                "recipe_product"
        );

        for (String table : tables) {
            database.execSQL("create view current_" + table + " as " +
                    "select * from " + table +
                    CurrentTable.NOW_AS_BEST_KNOWN
            );
        }

        database.execSQL("drop view " + ScaledAmount.SCALED_AMOUNT_TABLE);
        createView(database, ScaledAmount.SCALED_AMOUNT_TABLE, ScaledAmount.QUERY);
        createView(database, ScaledUnitConversion.SCALED_UNIT_CONVERSION_TABLE, ScaledUnitConversion.QUERY);
        createView(database, RecipeStockRatingBase.RECIPE_STOCK_RATING_BASE_TABLE, RecipeStockRatingBase.QUERY);
        createView(database, RecipeIngredientAmountAndStock.RECIPE_INGREDIENT_AMOUNT_AND_STOCK_TABLE, RecipeIngredientAmountAndStock.QUERY);
    }

    private void createView(SupportSQLiteDatabase database, String name, String query) {
        database.execSQL("create view " + name + " as " + query);
    }
}
