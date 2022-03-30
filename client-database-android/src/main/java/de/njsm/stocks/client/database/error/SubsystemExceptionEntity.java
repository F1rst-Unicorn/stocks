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

package de.njsm.stocks.client.database.error;

import androidx.room.Entity;
import androidx.room.Ignore;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.database.IdFields;

@Entity(tableName = "subsystem_error")
@AutoValue
public abstract class SubsystemExceptionEntity implements IdFields, SubsystemExceptionEntityFields {

    static SubsystemExceptionEntity create(int id,
                                           String stacktrace,
                                           String message) {
        return new AutoValue_SubsystemExceptionEntity(id, stacktrace, message);
    }

    @Ignore
    static SubsystemExceptionEntity create(String stacktrace,
                                           String message) {
        return new AutoValue_SubsystemExceptionEntity(0, stacktrace, message);
    }
}
