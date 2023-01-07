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
import androidx.room.ColumnInfo;
import com.google.auto.value.AutoValue;

import java.time.Instant;

abstract class ServerDbEntity<T extends ServerDbEntity<T>> {

    @ColumnInfo(name = "id")
    @AutoValue.CopyAnnotations
    public abstract int id();

    @ColumnInfo(name = "version")
    @AutoValue.CopyAnnotations
    public abstract int version();

    @ColumnInfo(name = "valid_time_start")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract Instant validTimeStart();

    @ColumnInfo(name = "valid_time_end")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract Instant validTimeEnd();

    @ColumnInfo(name = "transaction_time_start")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract Instant transactionTimeStart();

    @ColumnInfo(name = "transaction_time_end")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract Instant transactionTimeEnd();

    @ColumnInfo(name = "initiates")
    @AutoValue.CopyAnnotations
    public abstract int initiates();

    abstract <B extends Builder<T, B>> B toBuilder();

    public abstract static class Builder<E extends ServerDbEntity<E>, B extends ServerDbEntity.Builder<E, B>> {

        public abstract B id(int v);

        public abstract B version(int v);

        public abstract B validTimeStart(Instant v);

        public abstract B validTimeEnd(Instant v);

        public abstract B transactionTimeStart(Instant v);

        public abstract B transactionTimeEnd(Instant v);

        public abstract B initiates(int v);

        abstract E build();
    }
}
