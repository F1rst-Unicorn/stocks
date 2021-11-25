/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General private License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General private License for more details.
 *
 * You should have received a copy of the GNU General private License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.android.rewrite.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import java.time.Instant;

@Entity(tableName = "location", primaryKeys = {"_id", "version", "transaction_time_start"},
        indices = {
                @Index(value = {"_id", "valid_time_start", "valid_time_end"}, name = "location_current"),
                @Index(value = {"_id"}, name = "location_pkey"),
                @Index(value = {"transaction_time_start"}, name = "location_transaction_time_start"),
                @Index(value = {"transaction_time_end"}, name = "location_transaction_time_end"),
        })
class LocationDbEntity extends DbEntity {

    @ColumnInfo(name = "name")
    @NonNull
    private final String name;

    @ColumnInfo(name = "description")
    @NonNull
    private final String description;

    LocationDbEntity(int id, int version, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int initiates, String name, @NonNull String description) {
        super(id, version, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, initiates);
        this.name = name;
        this.description = description;
    }

    String getName() {
        return name;
    }

    @NonNull
    String getDescription() {
        return description;
    }
}
