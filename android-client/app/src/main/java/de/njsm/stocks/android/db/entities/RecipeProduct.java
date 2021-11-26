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
import androidx.room.Entity;
import androidx.room.Index;

import java.time.Instant;
import java.util.Objects;

@Entity(tableName = "recipe_product",
        primaryKeys = {"_id", "version", "transaction_time_start"},
        indices = {
                @Index(value = {"_id", "valid_time_start", "valid_time_end"}, name = "recipe_product_current"),
                @Index(value = {"_id"}, name = "recipe_product_pkey"),
                @Index(value = {"transaction_time_start"}, name = "recipe_product_transaction_time_start"),
                @Index(value = {"transaction_time_end"}, name = "recipe_product_transaction_time_end"),
        })
public class RecipeProduct extends VersionedData {

    @ColumnInfo(name = "amount")
    @NonNull
    public int amount;

    @ColumnInfo(name = "product")
    @NonNull
    public int product;

    @ColumnInfo(name = "recipe")
    @NonNull
    public int recipe;

    @ColumnInfo(name = "unit")
    @NonNull
    public int unit;

    public RecipeProduct(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, int amount, int product, int recipe, int unit) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates);
        this.amount = amount;
        this.product = product;
        this.recipe = recipe;
        this.unit = unit;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public int getRecipe() {
        return recipe;
    }

    public void setRecipe(int recipe) {
        this.recipe = recipe;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RecipeProduct that = (RecipeProduct) o;
        return getAmount() == that.getAmount() && getProduct() == that.getProduct() && getRecipe() == that.getRecipe() && getUnit() == that.getUnit();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAmount(), getProduct(), getRecipe(), getUnit());
    }
}
