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
import de.njsm.stocks.client.business.ErrorRecorder;
import de.njsm.stocks.client.business.entities.StatusCode;

import java.util.Objects;

@Entity(tableName = "status_code_error")
class StatusCodeExceptionEntity extends SubsystemExceptionEntity {

    @ColumnInfo(name = "status_code")
    @NonNull
    private StatusCode statusCode;

    StatusCodeExceptionEntity(int id, @NonNull ErrorRecorder.Action action, @NonNull String stacktrace, @NonNull String message, @NonNull StatusCode statusCode) {
        super(id, action, stacktrace, message);
        this.statusCode = statusCode;
    }

    @NonNull
    StatusCode getStatusCode() {
        return statusCode;
    }

    void setStatusCode(@NonNull StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StatusCodeExceptionEntity that = (StatusCodeExceptionEntity) o;
        return getStatusCode() == that.getStatusCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getStatusCode());
    }
}
