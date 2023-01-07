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

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.auto.value.AutoValue;

import java.time.Instant;

@AutoValue
@Entity(tableName = "recent_search")
public abstract class RecentSearchDbEntity {

    @PrimaryKey
    @ColumnInfo(name = "term")
    @AutoValue.CopyAnnotations
    @NonNull
    public abstract String term();

    @ColumnInfo(name = "last_queried")
    @AutoValue.CopyAnnotations
    @NonNull
    public abstract Instant lastQueried();

    public static RecentSearchDbEntity create(String term, Instant lastQueried) {
        return new AutoValue_RecentSearchDbEntity(term, lastQueried);
    }
}
