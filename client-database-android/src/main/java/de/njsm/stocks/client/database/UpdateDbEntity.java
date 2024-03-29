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
import androidx.room.Ignore;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.entities.EntityType;

import java.time.Instant;


@Entity(tableName = "updates")
@AutoValue
public abstract class UpdateDbEntity implements IdFields {

    @ColumnInfo(name = "name")
    @NonNull
    @AutoValue.CopyAnnotations
    abstract EntityType table();

    @ColumnInfo(name = "last_update")
    @NonNull
    @AutoValue.CopyAnnotations
    abstract Instant lastUpdate();

    static UpdateDbEntity create(int id, EntityType table, Instant lastUpdate) {
        return new AutoValue_UpdateDbEntity(id, table, lastUpdate);
    }

    @Ignore
    public static UpdateDbEntity create(EntityType table, Instant lastUpdate) {
        return create(0, table, lastUpdate);
    }
}
