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

package de.njsm.stocks.client.database;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase.Callback;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class PerformanceTweaker extends Callback {

    @Override
    public void onOpen(@NonNull SupportSQLiteDatabase db) {
        makeCurrentIndicesSelective(db);
        db.execSQL("analyze");
    }

    private void makeCurrentIndicesSelective(SupportSQLiteDatabase db) {
        recreateCurrentIndex(db, "user");
        recreateCurrentIndex(db, "user_device");
        recreateCurrentIndex(db, "location");
        recreateCurrentIndex(db, "unit");
        recreateCurrentIndex(db, "scaled_unit");
        recreateCurrentIndex(db, "food");
        recreateCurrentIndex(db, "food_item");
        recreateCurrentIndex(db, "ean_number");
        recreateCurrentIndex(db, "recipe");
        recreateCurrentIndex(db, "recipe_ingredient");
        recreateCurrentIndex(db, "recipe_product");

        recreateFoodToBuyIndex(db);
    }

    private static void recreateCurrentIndex(SupportSQLiteDatabase db, String tableName) {
        try (var cursor = db.query("select sql from sqlite_master " +
                "where name = '" + tableName + "_current'")) {
            cursor.moveToNext();
            String sql = cursor.getString(0);
            if (!sql.contains("where")) {
                db.execSQL("drop index if exists " + tableName + "_current");
                db.execSQL("create index " + tableName + "_current " +
                        "on " + tableName + " (id, valid_time_start, valid_time_end) " +
                        "where transaction_time_end = " + StocksDatabase.DATABASE_INFINITY_STRING_SQL);
            }
        }
    }

    private void recreateFoodToBuyIndex(SupportSQLiteDatabase db) {
        try (var cursor = db.query("select sql from sqlite_master " +
                "where name = 'food_current_to_buy'")) {
            cursor.moveToNext();
            String sql = cursor.getString(0);
            if (!sql.contains("where to_buy")) {
                db.execSQL("drop index if exists food_current_to_buy");
                db.execSQL("create index food_current_to_buy " +
                        "on food (id, valid_time_start, valid_time_end, to_buy) " +
                        "where to_buy " +
                        "and transaction_time_end = " + StocksDatabase.DATABASE_INFINITY_STRING_SQL);
            }
        }
    }
}
