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
 *
 */

package de.njsm.stocks.client.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import de.njsm.stocks.client.database.StocksDatabase;

public class Migration45To46 extends Migration {

    public Migration45To46() {
        super(45, 46);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        db.execSQL("create index food_item_current_by_of_type " +
                "on food_item (of_type, valid_time_start, valid_time_end) " +
                "where transaction_time_end = " + StocksDatabase.DATABASE_INFINITY_STRING_SQL);
        db.execSQL("create index recipe_ingredient_current_by_recipe " +
                "on recipe_ingredient (recipe, valid_time_start, valid_time_end) " +
                "where transaction_time_end = " + StocksDatabase.DATABASE_INFINITY_STRING_SQL);
        db.execSQL("create index recipe_product_current_by_recipe " +
                "on recipe_product (recipe, valid_time_start, valid_time_end) " +
                "where transaction_time_end = " + StocksDatabase.DATABASE_INFINITY_STRING_SQL);
    }
}
