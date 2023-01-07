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
import androidx.room.Index;
import com.google.auto.value.AutoValue;

import java.time.Instant;

@Entity(tableName = "recipe_product", primaryKeys = {"id", "version", "transaction_time_start"},
        indices = {
                @Index(value = {"id", "valid_time_start", "valid_time_end"}, name = "recipe_product_current"),
                @Index(value = {"id"}, name = "recipe_product_pkey"),
                @Index(value = {"transaction_time_start"}, name = "recipe_product_transaction_time_start"),
                @Index(value = {"transaction_time_end"}, name = "recipe_product_transaction_time_end"),
        })
@AutoValue
public abstract class RecipeProductDbEntity extends ServerDbEntity<RecipeProductDbEntity> implements RecipeProductFields {

    static Builder builder() {
        return new AutoValue_RecipeProductDbEntity.Builder();
    }

    public static RecipeProductDbEntity create(int id,
                                               int version,
                                               Instant validTimeStart,
                                               Instant validTimeEnd,
                                               Instant transactionTimeStart,
                                               Instant transactionTimeEnd,
                                               int initiates,
                                               int amount,
                                               int product,
                                               int unit,
                                               int recipe) {
        return new AutoValue_RecipeProductDbEntity.Builder()
                .id(id)
                .version(version)
                .validTimeStart(validTimeStart)
                .validTimeEnd(validTimeEnd)
                .transactionTimeStart(transactionTimeStart)
                .transactionTimeEnd(transactionTimeEnd)
                .initiates(initiates)
                .amount(amount)
                .product(product)
                .unit(unit)
                .recipe(recipe)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked") // Builder always complies to parent class
    abstract Builder toBuilder();

    @AutoValue.Builder
    abstract static class Builder extends ServerDbEntity.Builder<RecipeProductDbEntity, Builder> implements RecipeProductFields.Builder<RecipeProductDbEntity, Builder> {

        abstract RecipeProductDbEntity build();
    }
}
