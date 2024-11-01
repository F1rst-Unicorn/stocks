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

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.BaseColumns;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import de.njsm.stocks.client.business.entities.SearchedFoodForListingBaseData;
import de.njsm.stocks.client.business.entities.StoredFoodAmount;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
abstract class SearchDao {

    private static final String DIRECT_FOOD_SEARCH_COLUMNS =
            "select 0 as " + BaseColumns._ID + ", " +
            "f.name as " + SearchManager.SUGGEST_COLUMN_TEXT_1 + ", " +
            "f.name as " + SearchManager.SUGGEST_COLUMN_QUERY + ", " +
            "'" + Intent.ACTION_VIEW + "' as " + SearchManager.SUGGEST_COLUMN_INTENT_ACTION + ", " +
            "f.id as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID + ", " +
            "'" + ContentResolver.SCHEME_ANDROID_RESOURCE + "://' || :applicationId || '/drawable/ic_local_dining_black_24dp' as " + SearchManager.SUGGEST_COLUMN_ICON_1 + ", " +
            "null as " + SearchManager.SUGGEST_COLUMN_ICON_2 + ", ";
    private static final String UNSPECIFIC_SEARCH_COLUMNS =
            "select 0 as " + BaseColumns._ID + ", " +
            "s.term as " + SearchManager.SUGGEST_COLUMN_TEXT_1 + ", " +
            "s.term as " + SearchManager.SUGGEST_COLUMN_QUERY + ", " +
            "'" + Intent.ACTION_SEARCH + "' as " + SearchManager.SUGGEST_COLUMN_INTENT_ACTION + ", " +
            "null as " + SearchManager.SUGGEST_COLUMN_ICON_1 + ", " +
            "'" + ContentResolver.SCHEME_ANDROID_RESOURCE + "://' || :applicationId || '/drawable/ic_menu_recent_history_24dp' as " + SearchManager.SUGGEST_COLUMN_ICON_2 + ", " +
            "null as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID + ", ";

    private static final String FOOD_BY_EXACT_NAME = "0 as type, " +
            "null as time " +
            "from current_food f " +
            "where upper(f.name) = upper(:query) ";
    private static final String RECENT_FOOD_BY_CONTIGUOUS = "1 as type, " +
            "s.last_queried as time " +
            "from searched_food s " +
            "join current_food f on f.id = s.food " +
            "where f.name like :contiguousQuery " +
            "and upper(f.name) != upper(:query) ";
    private static final String RECENT_FOOD_BY_SUBSEQUENCE = "1 as type, " +
            "s.last_queried as time " +
            "from searched_food s " +
            "join current_food f on f.id = s.food " +
            "where f.name like :subsequenceQuery " +
            "and f.name not like :contiguousQuery " +
            "and upper(f.name) != upper(:query) ";
    private static final String FOOD_BY_CONTIGUOUS = "3 as type, " +
            "null as time " +
            "from current_food f " +
            "where f.name like :contiguousQuery " +
            "and upper(f.name) != upper(:query) " +
            "and f.id not in (" +
                "select food " +
                "from searched_food" +
            ")";
    private static final String FOOD_BY_SUBSEQUENCE = "4 as type, " +
            "null as time " +
            "from current_food f " +
            "where f.name like :subsequenceQuery " +
            "and f.name not like :contiguousQuery " +
            "and upper(f.name) != upper(:query) " +
                "and f.id not in (" +
                "select food " +
                "from searched_food" +
            ")";

    /**
     * There are 7 categories
     * <ul>
     *     <li>0: food matched exactly by the search query</li>
     *     <li>1: recently searched food containing the query in a contiguous substring</li>
     *     <li>1: recently searched food containing the query in a subsequence</li>
     *     <li>1: recently searched terms containing the query in a contiguous substring</li>
     *     <li>2: recently searched terms containing the query in a subsequence</li>
     *     <li>3: not-searched food containing the query in a contiguous substring</li>
     *     <li>4: not-searched food containing the query in a subsequence</li>
     * </ul>
     *
     * The number indicates a sort order. Result rows in the same sort order are
     * sorted by last time searched (if defined) and finally by longest name
     * first.
     */
    @Query("select * from (" +
                "select * from ( " +
                DIRECT_FOOD_SEARCH_COLUMNS +
                FOOD_BY_EXACT_NAME +
            ") union all select * from (" +
                DIRECT_FOOD_SEARCH_COLUMNS +
                RECENT_FOOD_BY_CONTIGUOUS +
            ") union all select * from (" +
                DIRECT_FOOD_SEARCH_COLUMNS +
                RECENT_FOOD_BY_SUBSEQUENCE +
            ") union all select * from (" +
                UNSPECIFIC_SEARCH_COLUMNS +
                "1 as type ," +
                "s.last_queried as time " +
                "from recent_search s " +
                "where s.term like :contiguousQuery " +
            ") union all select * from (" +
                UNSPECIFIC_SEARCH_COLUMNS +
                "2 as type ," +
                "s.last_queried as time " +
                "from recent_search s " +
                "where s.term like :subsequenceQuery " +
                "and s.term not like :contiguousQuery " +
            ") union all select * from (" +
                DIRECT_FOOD_SEARCH_COLUMNS +
                FOOD_BY_CONTIGUOUS +
            ") union all select * from (" +
                DIRECT_FOOD_SEARCH_COLUMNS +
                FOOD_BY_SUBSEQUENCE +
        ")) " +
        "order by type, time desc, length(" + SearchManager.SUGGEST_COLUMN_TEXT_1 + ") desc")
    abstract Cursor search(String applicationId, String query, String contiguousQuery, String subsequenceQuery);

    @Insert(onConflict = REPLACE)
    abstract void store(RecentSearchDbEntity searchSuggestion);

    @Insert(onConflict = REPLACE)
    abstract void store(SearchedFoodDbEntity searchSuggestion);

    private static final String FOOD_SEARCH_QUERY = "select * from ( " +
                "select id, name, store_unit, to_buy as toBuy," +
                FOOD_BY_EXACT_NAME +
            ") union all select * from (" +
                "select id, name, store_unit, to_buy as toBuy," +
                RECENT_FOOD_BY_CONTIGUOUS +
            ") union all select * from (" +
                "select id, name, store_unit, to_buy as toBuy," +
                RECENT_FOOD_BY_SUBSEQUENCE +
            ") union all select * from (" +
                "select id, name, store_unit, to_buy as toBuy," +
                FOOD_BY_CONTIGUOUS +
            ") union all select * from (" +
                "select id, name, store_unit, to_buy as toBuy," +
                FOOD_BY_SUBSEQUENCE +
            ")";

    @Query("select id, name, toBuy from (" +
            FOOD_SEARCH_QUERY + ") " +
            "order by type, time desc, length(name) desc")
    abstract Observable<List<SearchedFoodForListingBaseData>> getFoodBy(String query, String contiguousQuery, String subsequenceQuery);

    @Query("select i.of_type as foodId, s.id as scaledUnitId, u.id as unitId, " +
            "count(1) as numberOfFoodItemsWithSameScaledUnit, s.scale as scale, u.abbreviation as abbreviation " +
            "from current_food_item i " +
            "join (select id from (" + FOOD_SEARCH_QUERY + ")) f on i.of_type = f.id " +
            "join current_scaled_unit s on i.unit = s.id " +
            "join current_unit u on s.unit = u.id " +
            "group by i.of_type, s.id, u.id, s.scale, u.abbreviation")
    abstract Observable<List<StoredFoodAmount>> getFoodAmountsIn(String query, String contiguousQuery, String subsequenceQuery);

    @Query("select f.id as foodId, s.id as scaledUnitId, u.id as unitId, " +
            "0 as numberOfFoodItemsWithSameScaledUnit, s.scale as scale, u.abbreviation as abbreviation " +
            "from (select id, store_unit from (" + FOOD_SEARCH_QUERY + ")) f " +
            "join current_scaled_unit s on f.store_unit = s.id " +
            "join current_unit u on s.unit = u.id " +
            "and f.id not in (" +
                "select of_type " +
                "from current_food_item" +
            ")")
    abstract Observable<List<StoredFoodAmount>> getFoodAmountsOfAbsentFood(String query, String contiguousQuery, String subsequenceQuery);

    @Query("select * " +
            "from recent_search")
    abstract List<RecentSearchDbEntity> getRecentSearches();

    @Query("select * " +
            "from searched_food")
    abstract List<SearchedFoodDbEntity> getSearchedFood();

    @Query("delete from recent_search")
    abstract void deleteRecentSearches();

    @Query("delete from searched_food")
    abstract void deleteSearchedFood();
}
