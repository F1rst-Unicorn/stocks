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

package de.njsm.stocks.android.contentprovider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.android.AndroidInjection;
import de.njsm.stocks.android.db.dao.SearchSuggestionDao;
import de.njsm.stocks.android.db.entities.SearchSuggestion;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;

public class RecentSearchSuggestionsProvider extends ContentProvider {

    private static final Logger LOG = new Logger(RecentSearchSuggestionsProvider.class);

    private final static String AUTHORITY = "de.njsm.stocks.android.contentprovider.RecentSearchSuggestionsProvider";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private SearchSuggestionDao searchSuggestionDao;

    private UriMatcher matcher;

    private boolean isInitialised = false;

    public static void saveSearchTerm(Context context, String term) {
        ContentValues value = new ContentValues();
        value.put("v", term);
        context.getContentResolver().insert(Uri.withAppendedPath(BASE_URI, "insert"), value);
    }

    public static void clearSearchHistory(Context context) {
        context.getContentResolver().delete(Uri.withAppendedPath(BASE_URI, "delete"), null, null);
    }

    @Inject
    public void setSearchSuggestionDao(SearchSuggestionDao searchSuggestionDao) {
        this.searchSuggestionDao = searchSuggestionDao;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    // Injection may not happen during onCreate() because content providers are
    // instantiated before the Application is ready to inject
    private void initialise() {
        if (isInitialised)
            return;

        AndroidInjection.inject(this);

        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, 0);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", 0);
        matcher.addURI(AUTHORITY, "delete", 1);
        matcher.addURI(AUTHORITY, "insert", 2);

        isInitialised = true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        initialise();
        if (matcher.match(uri) == 0) {
            String queryInput = uri.getLastPathSegment();
            if (queryInput == null || queryInput.equals(SearchManager.SUGGEST_URI_PATH_QUERY))
                queryInput = "";

            String contiguousQuery = buildContiguousQueryString(queryInput);
            String subsequenceQuery = buildSubsequenceQueryString(queryInput);

            LOG.d("Searching for " + queryInput);
            return searchSuggestionDao.getFoodBySubStringJoiningStoredSuggestions(contiguousQuery, subsequenceQuery);
        } else {
            throw new IllegalArgumentException("Uri: " + uri.toString());
        }
    }

    private String buildContiguousQueryString(String query) {
        return '%' + query + '%';
    }

    private String buildSubsequenceQueryString(String query) {
        StringBuilder queryBuilder = new StringBuilder(query.length() * 2 + 1);
        queryBuilder.append('%');
        for (char c : query.toCharArray()) {
            queryBuilder.append(c);
            queryBuilder.append('%');
        }
        return queryBuilder.toString();
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        initialise();
        if (values == null) {
            return null;
        }

        if (matcher.match(uri) == 2) {
            SearchSuggestion suggestion = new SearchSuggestion(values.getAsString("v"));
            searchSuggestionDao.insert(suggestion);
        } else {
            throw new IllegalArgumentException("Uri: " + uri.toString());
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        initialise();
        if (matcher.match(uri) == 1) {
            searchSuggestionDao.delete();
        } else {
            throw new IllegalArgumentException("Uri: " + uri.toString());
        }

        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
