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

package de.njsm.stocks.client.database;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import java.time.Instant;
import java.util.Objects;

abstract class DbEntity {

    @ColumnInfo(name = "_id")
    private int id;

    @ColumnInfo(name = "version")
    private int version;

    @ColumnInfo(name = "valid_time_start")
    @NonNull
    private Instant validTimeStart;

    @ColumnInfo(name = "valid_time_end")
    @NonNull
    private Instant validTimeEnd;

    @ColumnInfo(name = "transaction_time_start")
    @NonNull
    private Instant transactionTimeStart;

    @ColumnInfo(name = "transaction_time_end")
    @NonNull
    private Instant transactionTimeEnd;

    @ColumnInfo(name = "initiates")
    private int initiates;

    DbEntity(int id, int version, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int initiates) {
        this.id = id;
        this.validTimeStart = validTimeStart;
        this.validTimeEnd = validTimeEnd;
        this.transactionTimeStart = transactionTimeStart;
        this.transactionTimeEnd = transactionTimeEnd;
        this.version = version;
        this.initiates = initiates;
    }

    int getId() {
        return id;
    }

    int getVersion() {
        return version;
    }

    @NonNull
    Instant getValidTimeStart() {
        return validTimeStart;
    }

    @NonNull
    Instant getValidTimeEnd() {
        return validTimeEnd;
    }

    @NonNull
    Instant getTransactionTimeStart() {
        return transactionTimeStart;
    }

    @NonNull
    Instant getTransactionTimeEnd() {
        return transactionTimeEnd;
    }

    int getInitiates() {
        return initiates;
    }

    void setId(int id) {
        this.id = id;
    }

    void setVersion(int version) {
        this.version = version;
    }

    void setValidTimeStart(@NonNull Instant validTimeStart) {
        this.validTimeStart = validTimeStart;
    }

    void setValidTimeEnd(@NonNull Instant validTimeEnd) {
        this.validTimeEnd = validTimeEnd;
    }

    void setTransactionTimeStart(@NonNull Instant transactionTimeStart) {
        this.transactionTimeStart = transactionTimeStart;
    }

    void setTransactionTimeEnd(@NonNull Instant transactionTimeEnd) {
        this.transactionTimeEnd = transactionTimeEnd;
    }

    void setInitiates(int initiates) {
        this.initiates = initiates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DbEntity dbEntity = (DbEntity) o;
        return getId() == dbEntity.getId() && getVersion() == dbEntity.getVersion() && getInitiates() == dbEntity.getInitiates() && getValidTimeStart().equals(dbEntity.getValidTimeStart()) && getValidTimeEnd().equals(dbEntity.getValidTimeEnd()) && getTransactionTimeStart().equals(dbEntity.getTransactionTimeStart()) && getTransactionTimeEnd().equals(dbEntity.getTransactionTimeEnd());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVersion(), getValidTimeStart(), getValidTimeEnd(), getTransactionTimeStart(), getTransactionTimeEnd(), getInitiates());
    }
}
