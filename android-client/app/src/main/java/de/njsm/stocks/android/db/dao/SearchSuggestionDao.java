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

@Dao
public abstract class SearchSuggestionDao {

    @Query("delete from search_suggestion")
    public abstract void delete();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(SearchSuggestion suggestion);


    @Query("select * from (" +
                "select * from ( " +
                    "select 0 as " + BaseColumns._ID + ", " +
                    "f.name as " + SearchManager.SUGGEST_COLUMN_TEXT_1 + ", " +
                    "f.name as " + SearchManager.SUGGEST_COLUMN_QUERY + ", " +
                    "'" + Intent.ACTION_VIEW + "' as " + SearchManager.SUGGEST_COLUMN_INTENT_ACTION + ", " +
                    "f._id as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID + ", " +
                    "'" + ContentResolver.SCHEME_ANDROID_RESOURCE + "://de.njsm.stocks/drawable/ic_local_dining_black_24dp' as " + SearchManager.SUGGEST_COLUMN_ICON_1 + ", " +
                    "null as " + SearchManager.SUGGEST_COLUMN_ICON_2 + ", " +
                    "2 as type, " +
                    "null as time " +
                    "from Food f " +
                    "where f.name like :searchTerm " +
                    "order by length(" + SearchManager.SUGGEST_COLUMN_TEXT_1 + ") desc " +
                    "limit 6" +
                ") union select * from (" +
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
                    "where s.term like :searchTerm " +
                    "order by s.last_queried desc " +
                    "limit 4" +
                ")" +
            ") " +
            "order by type, time desc, length(" + SearchManager.SUGGEST_COLUMN_TEXT_1 + ") desc")
    public abstract Cursor getFoodBySubStringJoiningStoredSuggestions(String searchTerm);
}
