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
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.threeten.bp.Instant;

@Entity(tableName = "search_suggestion")
public class SearchSuggestion {

    @PrimaryKey
    @ColumnInfo(name = "term")
    @NonNull
    public String term;

    @ColumnInfo(name = "last_queried")
    @NonNull
    public Instant lastQueried;

    public SearchSuggestion(@NonNull String term, Instant lastQueried) {
        this.term = term;
        this.lastQueried = lastQueried;
    }

    @Ignore
    public SearchSuggestion(@NonNull String term) {
        this.term = term;
        this.lastQueried = Instant.now();
    }
}
