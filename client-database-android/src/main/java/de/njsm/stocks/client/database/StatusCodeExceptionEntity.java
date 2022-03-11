/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General License for more details.
 *
 * You should have received a copy of the GNU General License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.client.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.ErrorRecorder;
import de.njsm.stocks.client.business.entities.StatusCode;

@Entity(tableName = "status_code_error")
@AutoValue
abstract class StatusCodeExceptionEntity implements IdFields, SubsystemExceptionEntityFields {

    @ColumnInfo(name = "status_code")
    @NonNull
    @AutoValue.CopyAnnotations
    abstract StatusCode statusCode();

    static StatusCodeExceptionEntity create(int id,
                                           ErrorRecorder.Action action,
                                           String stacktrace,
                                           String message,
                                           StatusCode statusCode) {
        return new AutoValue_StatusCodeExceptionEntity(id, action, stacktrace, message, statusCode);
    }

    @Ignore
    static StatusCodeExceptionEntity create(ErrorRecorder.Action action,
                                           String stacktrace,
                                           String message,
                                           StatusCode statusCode) {
        return new AutoValue_StatusCodeExceptionEntity(0, action, stacktrace, message, statusCode);
    }
}
