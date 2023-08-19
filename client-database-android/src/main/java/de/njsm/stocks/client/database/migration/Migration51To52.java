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

public class Migration51To52 extends Migration {

    public Migration51To52() {
        super(51, 52);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        db.execSQL("delete from status_code_error " +
                "where id in (" +
                    "select exception_id " +
                    "from error " +
                    "where data_id in (" +
                        "select id " +
                        "from food_item_to_add " +
                        "where stored_in_id = -1" +
                    ")" +
                    "and \"action\" = 'ADD_FOOD_ITEM'" +
                    "and exception_type = 'STATUSCODE_EXCEPTION'" +
                ")");
        db.execSQL("delete from subsystem_error " +
                "where id in (" +
                    "select exception_id " +
                    "from error " +
                    "where data_id in (" +
                        "select id " +
                        "from food_item_to_add " +
                        "where stored_in_id = -1" +
                    ")" +
                    "and \"action\" = 'ADD_FOOD_ITEM'" +
                    "and exception_type = 'SUBSYSTEM_EXCEPTION'" +
                ")");
        db.execSQL("delete from error " +
                "where data_id in (" +
                    "select id " +
                    "from food_item_to_add " +
                    "where stored_in_id = -1" +
                ") " +
                "and action = 'ADD_FOOD_ITEM'");
        db.execSQL("delete from food_item_to_add " +
                "where stored_in_id = -1");
    }
}
