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
import androidx.room.Ignore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.threeten.bp.Instant;

import de.njsm.stocks.android.network.server.util.InstantDeserialiser;
import de.njsm.stocks.android.network.server.util.InstantSerialiser;

public class VersionedData extends Data {

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    @ColumnInfo(name = "valid_time_start", defaultValue = "")
    @NonNull
    public Instant validTimeStart;

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    @ColumnInfo(name = "valid_time_end", defaultValue = "")
    @NonNull
    public Instant validTimeEnd;

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    @ColumnInfo(name = "transaction_time_start", defaultValue = "")
    @NonNull
    public Instant transactionTimeStart;

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    @ColumnInfo(name = "transaction_time_end", defaultValue = "")
    @NonNull
    public Instant transactionTimeEnd;

    @ColumnInfo(name = "version")
    public int version;

    @Ignore
    public VersionedData() {}

    public VersionedData(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version) {
        super(id);
        this.validTimeStart = validTimeStart;
        this.validTimeEnd = validTimeEnd;
        this.transactionTimeStart = transactionTimeStart;
        this.transactionTimeEnd = transactionTimeEnd;
        this.version = version;
    }
}
