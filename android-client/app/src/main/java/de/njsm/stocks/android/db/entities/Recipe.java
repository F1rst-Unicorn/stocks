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

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Entity(tableName = "recipe",
        primaryKeys = {"_id", "version", "transaction_time_start"},
        indices = {
                @Index(value = {"_id", "valid_time_start", "valid_time_end"}, name = "recipe_current"),
                @Index(value = {"_id"}, name = "recipe_pkey"),
                @Index(value = {"transaction_time_start"}, name = "recipe_transaction_time_start"),
                @Index(value = {"transaction_time_end"}, name = "recipe_transaction_time_end"),
        })
public class Recipe extends VersionedData {

    @ColumnInfo(name = "name")
    @NonNull
    public String name;

    @ColumnInfo(name = "instructions")
    @NonNull
    public String instructions;

    @ColumnInfo(name = "duration")
    @NonNull
    public Duration duration;

    public Recipe(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, @NonNull String name, @NonNull String instructions, @NonNull Duration duration) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates);
        this.name = name;
        this.instructions = instructions;
        this.duration = duration;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(@NonNull String instructions) {
        this.instructions = instructions;
    }

    @NonNull
    public Duration getDuration() {
        return duration;
    }

    public void setDuration(@NonNull Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Recipe recipe = (Recipe) o;
        return getName().equals(recipe.getName()) && getInstructions().equals(recipe.getInstructions()) && getDuration().equals(recipe.getDuration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getInstructions(), getDuration());
    }
}
