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
import androidx.room.PrimaryKey;
import de.njsm.stocks.client.business.ErrorRecorder;

import java.util.Objects;

@Entity(tableName = "subsystem_error")
class SubsystemExceptionEntity {

    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "action")
    @NonNull
    private ErrorRecorder.Action action;

    @ColumnInfo(name = "stacktrace")
    @NonNull
    private String stacktrace;

    @ColumnInfo(name = "message")
    @NonNull
    private String message;

    SubsystemExceptionEntity(int id,
                                    @NonNull ErrorRecorder.Action action,
                                    @NonNull String stacktrace,
                                    @NonNull String message) {
        this.id = id;
        this.action = action;
        this.stacktrace = stacktrace;
        this.message = message;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    @NonNull
    ErrorRecorder.Action getAction() {
        return action;
    }

    void setAction(@NonNull ErrorRecorder.Action action) {
        this.action = action;
    }

    @NonNull
    String getStacktrace() {
        return stacktrace;
    }

    void setStacktrace(@NonNull String stacktrace) {
        this.stacktrace = stacktrace;
    }

    @NonNull
    String getMessage() {
        return message;
    }

    void setMessage(@NonNull String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubsystemExceptionEntity that = (SubsystemExceptionEntity) o;
        return getId() == that.getId() && getAction() == that.getAction() && getStacktrace().equals(that.getStacktrace()) && getMessage().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAction(), getStacktrace(), getMessage());
    }
}
