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
import androidx.room.Entity;

import java.time.Instant;
import java.util.Objects;


@Entity(tableName = "updates", primaryKeys = {"_id"})
class UpdateDbEntity {

    @ColumnInfo(name = "_id")
    private int id;

    @ColumnInfo(name = "name")
    @NonNull
    private String table;

    @ColumnInfo(name = "last_update")
    @NonNull
    private Instant lastUpdate;

    UpdateDbEntity(int id, String table, Instant lastUpdate) {
        this.id = id;
        this.table = table;
        this.lastUpdate = lastUpdate;
    }

    int getId() {
        return id;
    }

    @NonNull
    String getTable() {
        return table;
    }

    @NonNull
    Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTable(@NonNull String table) {
        this.table = table;
    }

    public void setLastUpdate(@NonNull Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateDbEntity that = (UpdateDbEntity) o;
        return getId() == that.getId() && getTable().equals(that.getTable()) && getLastUpdate().equals(that.getLastUpdate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTable(), getLastUpdate());
    }
}
