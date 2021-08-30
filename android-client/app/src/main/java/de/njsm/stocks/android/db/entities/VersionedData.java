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

package de.njsm.stocks.android.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import java.time.Instant;

public class VersionedData extends Data {

    @ColumnInfo(name = "valid_time_start", defaultValue = "")
    @NonNull
    public Instant validTimeStart;

    @ColumnInfo(name = "valid_time_end", defaultValue = "")
    @NonNull
    public Instant validTimeEnd;

    @ColumnInfo(name = "transaction_time_start", defaultValue = "")
    @NonNull
    public Instant transactionTimeStart;

    @ColumnInfo(name = "transaction_time_end", defaultValue = "")
    @NonNull
    public Instant transactionTimeEnd;

    @ColumnInfo(name = "version")
    public int version;

    @ColumnInfo(name = "initiates")
    public int initiates;

    public VersionedData(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates) {
        super(id);
        this.validTimeStart = validTimeStart;
        this.validTimeEnd = validTimeEnd;
        this.transactionTimeStart = transactionTimeStart;
        this.transactionTimeEnd = transactionTimeEnd;
        this.version = version;
        this.initiates = initiates;
    }

    @NonNull
    public Instant getValidTimeStart() {
        return validTimeStart;
    }

    @NonNull
    public Instant getValidTimeEnd() {
        return validTimeEnd;
    }

    @NonNull
    public Instant getTransactionTimeStart() {
        return transactionTimeStart;
    }

    @NonNull
    public Instant getTransactionTimeEnd() {
        return transactionTimeEnd;
    }

    public int getVersion() {
        return version;
    }

    public int getInitiates() {
        return initiates;
    }
}
