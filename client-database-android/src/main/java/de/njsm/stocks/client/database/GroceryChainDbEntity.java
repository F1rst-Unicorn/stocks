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

package de.njsm.stocks.client.database;

import androidx.room.Entity;
import androidx.room.Index;
import com.google.auto.value.AutoValue;

import java.time.Instant;

@Entity(tableName = "grocery_chain", primaryKeys = {"id", "version", "transaction_time_start"},
        indices = {
                @Index(value = {"id", "valid_time_start", "valid_time_end"}, name = "grocery_chain_current"),
                @Index(value = {"id"}, name = "grocery_chain_pkey"),
                @Index(value = {"transaction_time_start"}, name = "grocery_chain_transaction_time_start"),
                @Index(value = {"transaction_time_end"}, name = "grocery_chain_transaction_time_end"),
        })
@AutoValue
public abstract class GroceryChainDbEntity extends ServerDbEntity<GroceryChainDbEntity> implements GroceryChainFields {

    static Builder builder() {
        return new AutoValue_GroceryChainDbEntity.Builder();
    }

    public static GroceryChainDbEntity create(int id,
                                              int version,
                                              Instant validTimeStart,
                                              Instant validTimeEnd,
                                              Instant transactionTimeStart,
                                              Instant transactionTimeEnd,
                                              int initiates,
                                              String name) {
        return new AutoValue_GroceryChainDbEntity.Builder()
                .id(id)
                .version(version)
                .validTimeStart(validTimeStart)
                .validTimeEnd(validTimeEnd)
                .transactionTimeStart(transactionTimeStart)
                .transactionTimeEnd(transactionTimeEnd)
                .initiates(initiates)
                .name(name)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked") // Builder always complies to parent class
    abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends ServerDbEntity.Builder<GroceryChainDbEntity, Builder> implements GroceryChainFields.Builder<GroceryChainDbEntity, Builder> {

        public abstract GroceryChainDbEntity build();
    }
}
