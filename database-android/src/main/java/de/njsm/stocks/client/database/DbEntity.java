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

abstract class DbEntity {

    @ColumnInfo(name = "_id")
    private final int id;

    @ColumnInfo(name = "version")
    private final int version;

    @ColumnInfo(name = "valid_time_start")
    @NonNull
    private final Instant validTimeStart;

    @ColumnInfo(name = "valid_time_end")
    @NonNull
    private final Instant validTimeEnd;

    @ColumnInfo(name = "transaction_time_start")
    @NonNull
    private final Instant transactionTimeStart;

    @ColumnInfo(name = "transaction_time_end")
    @NonNull
    private final Instant transactionTimeEnd;

    @ColumnInfo(name = "initiates")
    private final int initiates;

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
}
