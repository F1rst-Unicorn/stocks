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

public class Migration_31_to_32 extends androidx.room.migration.Migration {

    public Migration_31_to_32() {
        super(31, 32);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("alter table user add column initiates integer not null default 0");
        database.execSQL("alter table location add column initiates integer not null default 0");
        database.execSQL("alter table user_device add column initiates integer not null default 0");
        database.execSQL("alter table food add column initiates integer not null default 0");
        database.execSQL("alter table fooditem add column initiates integer not null default 0");
        database.execSQL("alter table eannumber add column initiates integer not null default 0");

        database.execSQL("delete from updates");
    }
}
