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

package de.njsm.stocks.client.database.error;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.database.IdFields;
import de.njsm.stocks.client.database.PreservedId;
import de.njsm.stocks.client.database.VersionFields;

import java.time.Instant;

@AutoValue
@Entity(tableName = "grocery_store_to_edit")
public abstract class GroceryStoreEditEntity implements IdFields, VersionFields {

    @Embedded(prefix = "grocery_store_")
    @AutoValue.CopyAnnotations
    @NonNull
    public abstract PreservedId groceryStore();

    @ColumnInfo(name = "name")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract String name();

    @Embedded(prefix = "grocery_chain_")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract PreservedId groceryChain();

    @ColumnInfo(name = "execution_time")
    @AutoValue.CopyAnnotations
    @NonNull
    public abstract Instant executionTime();

    public static GroceryStoreEditEntity create(int id, int version, PreservedId groceryStore, Instant executionTime, String name, PreservedId groceryChain) {
        return new AutoValue_GroceryStoreEditEntity(id, version, groceryStore, name, groceryChain, executionTime);
    }

    @Ignore
    public static GroceryStoreEditEntity create(int version, PreservedId groceryStore, Instant executionTime, String name, PreservedId groceryChain) {
        return new AutoValue_GroceryStoreEditEntity(0, version, groceryStore, name, groceryChain, executionTime);
    }
}
