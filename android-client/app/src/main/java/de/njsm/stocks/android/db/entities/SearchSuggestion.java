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
