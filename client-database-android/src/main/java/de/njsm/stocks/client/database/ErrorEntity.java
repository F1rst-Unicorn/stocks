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

import androidx.room.Entity;
import androidx.room.Ignore;
import com.google.auto.value.AutoValue;

@AutoValue
@Entity(tableName = "error")
abstract class ErrorEntity implements IdFields {

    static ErrorEntity create(int id, Action action, long dataId, ExceptionType exceptionType, long exceptionId) {
        return new AutoValue_ErrorEntity(id, action, dataId, exceptionType, exceptionId);
    }

    @Ignore
    static ErrorEntity create(Action action, long dataId, ExceptionType exceptionType, long exceptionId) {
        return new AutoValue_ErrorEntity(0, action, dataId, exceptionType, exceptionId);
    }

    abstract Action action();

    abstract long dataId();

    abstract ExceptionType exceptionType();

    abstract long exceptionId();

    enum Action {
        SYNCHRONISATION,

        ADD_LOCATION,
    }

    enum ExceptionType {

        SUBSYSTEM_EXCEPTION,

        STATUSCODE_EXCEPTION
    }
}

