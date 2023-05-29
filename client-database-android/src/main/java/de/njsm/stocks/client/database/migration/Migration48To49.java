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

public class Migration48To49 extends Migration {

    public Migration48To49() {
        super(48, 49);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        var ddlPrimitives = new DdlPrimitives(db);
        ddlPrimitives.createTable("recipe_to_delete", "id",
                "id INTEGER not null",
                "recipe_id INTEGER not null",
                "recipe_transaction_time TEXT not null");
    }
}
