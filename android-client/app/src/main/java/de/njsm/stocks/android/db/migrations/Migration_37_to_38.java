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
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Arrays;
import java.util.List;

import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY_STRING;

public class Migration_37_to_38 extends androidx.room.migration.Migration {

    public Migration_37_to_38() {
        super(37, 38);
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
            database.execSQL(
                    "create index " + table + "_current " +
                    "on " + table + " (_id, valid_time_start, valid_time_end) " +
                    "where transaction_time_end = '" + DATABASE_INFINITY_STRING + "';"
            );
            database.execSQL(
                    "create index " + table + "_transaction_time_start " +
                    "on " + table + " (transaction_time_start);"
            );
            database.execSQL(
                    "create index " + table + "_transaction_time_end " +
                    "on " + table + " (transaction_time_end);"
            );
            database.execSQL(
                    "create index " + table + "_pkey " +
                    "on " + table + " (_id);"
            );
        }
    }
}
