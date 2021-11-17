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

package de.njsm.stocks.android.db.dao;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.BaseColumns;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import de.njsm.stocks.android.db.entities.SearchSuggestion;

import java.time.Instant;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;
import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
public abstract class SearchSuggestionDao {

    @Query("delete from search_suggestion")
    public abstract void delete();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(SearchSuggestion suggestion);

    public Cursor getFoodBySubStringJoiningStoredSuggestions(String contiguousQuery, String subsequenceQuery) {
        return getFoodBySubStringJoiningStoredSuggestions(contiguousQuery, subsequenceQuery, DATABASE_INFINITY);
    }

    @Query("select * from (" +
                "select * from ( " +
                    "select 0 as " + BaseColumns._ID + ", " +
                    "f.name as " + SearchManager.SUGGEST_COLUMN_TEXT_1 + ", " +
                    "f.name as " + SearchManager.SUGGEST_COLUMN_QUERY + ", " +
                    "'" + Intent.ACTION_VIEW + "' as " + SearchManager.SUGGEST_COLUMN_INTENT_ACTION + ", " +
                    "f._id as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID + ", " +
                    "'" + ContentResolver.SCHEME_ANDROID_RESOURCE + "://de.njsm.stocks/drawable/ic_local_dining_black_24dp' as " + SearchManager.SUGGEST_COLUMN_ICON_1 + ", " +
                    "null as " + SearchManager.SUGGEST_COLUMN_ICON_2 + ", " +
                    "3 as type, " +
                    "null as time " +
                    "from Food f " +
                    "where f.name like :contiguousQuery " +
                    "and f.valid_time_start <= " + NOW +
                    "and " + NOW + " < f.valid_time_end " +
                    "and f.transaction_time_end = :infinity " +
                    "order by length(" + SearchManager.SUGGEST_COLUMN_TEXT_1 + ") desc " +
                    "limit 6" +
                ") union all select * from (" +
                    "select 0 as " + BaseColumns._ID + ", " +
                    "s.term as " + SearchManager.SUGGEST_COLUMN_TEXT_1 + ", " +
                    "s.term as " + SearchManager.SUGGEST_COLUMN_QUERY + ", " +
                    "'" + Intent.ACTION_SEARCH + "' as " + SearchManager.SUGGEST_COLUMN_INTENT_ACTION + ", " +
                    "null as " + SearchManager.SUGGEST_COLUMN_ICON_1 + ", " +
                    "'" + ContentResolver.SCHEME_ANDROID_RESOURCE + "://de.njsm.stocks/drawable/ic_menu_recent_history_24dp' as " + SearchManager.SUGGEST_COLUMN_ICON_2 + ", " +
                    "null as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID + ", " +
                    "1 as type ," +
                    "s.last_queried as time " +
                    "from Search_suggestion s " +
                    "where s.term like :contiguousQuery " +
                    "order by s.last_queried desc " +
                    "limit 10" +
                ") union all select * from (" +
                    "select 0 as " + BaseColumns._ID + ", " +
                    "f.name as " + SearchManager.SUGGEST_COLUMN_TEXT_1 + ", " +
                    "f.name as " + SearchManager.SUGGEST_COLUMN_QUERY + ", " +
                    "'" + Intent.ACTION_VIEW + "' as " + SearchManager.SUGGEST_COLUMN_INTENT_ACTION + ", " +
                    "f._id as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID + ", " +
                    "'" + ContentResolver.SCHEME_ANDROID_RESOURCE + "://de.njsm.stocks/drawable/ic_local_dining_black_24dp' as " + SearchManager.SUGGEST_COLUMN_ICON_1 + ", " +
                    "null as " + SearchManager.SUGGEST_COLUMN_ICON_2 + ", " +
                    "4 as type, " +
                    "null as time " +
                    "from Food f " +
                    "where f.name like :subsequenceQuery " +
                    "and f.name not like :contiguousQuery " +
                    "and f.valid_time_start <= " + NOW +
                    "and " + NOW + " < f.valid_time_end " +
                    "and f.transaction_time_end = :infinity " +
                    "order by length(" + SearchManager.SUGGEST_COLUMN_TEXT_1 + ") desc " +
                    "limit 6" +
                ") union all select * from (" +
                    "select 0 as " + BaseColumns._ID + ", " +
                    "s.term as " + SearchManager.SUGGEST_COLUMN_TEXT_1 + ", " +
                    "s.term as " + SearchManager.SUGGEST_COLUMN_QUERY + ", " +
                    "'" + Intent.ACTION_SEARCH + "' as " + SearchManager.SUGGEST_COLUMN_INTENT_ACTION + ", " +
                    "null as " + SearchManager.SUGGEST_COLUMN_ICON_1 + ", " +
                    "'" + ContentResolver.SCHEME_ANDROID_RESOURCE + "://de.njsm.stocks/drawable/ic_menu_recent_history_24dp' as " + SearchManager.SUGGEST_COLUMN_ICON_2 + ", " +
                    "null as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID + ", " +
                    "2 as type ," +
                    "s.last_queried as time " +
                    "from Search_suggestion s " +
                    "where s.term like :subsequenceQuery " +
                    "and s.term not like :contiguousQuery " +
                    "order by s.last_queried desc " +
                    "limit 10" +
                ")" +
            ") " +
            "order by type, time desc, length(" + SearchManager.SUGGEST_COLUMN_TEXT_1 + ") desc")
    abstract Cursor getFoodBySubStringJoiningStoredSuggestions(String contiguousQuery, String subsequenceQuery, Instant infinity);
}
