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

package de.njsm.stocks.client.database.contentprovider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.android.AndroidInjection;
import de.njsm.stocks.client.database.SearchRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SearchSuggestionsProvider extends ContentProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SearchSuggestionsProvider.class);

    private final static String AUTHORITY = ".client.database.contentprovider.SearchSuggestionsProvider";

    private SearchRepositoryImpl repository;

    private UriMatcher matcher;

    private boolean isInitialised = false;

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
        matcher.addURI(getContext().getPackageName() + AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, 0);
        matcher.addURI(getContext().getPackageName() + AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", 0);

        isInitialised = true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        initialise();
        LOG.trace(uri.toString());
        switch (matcher.match(uri)) {
            case 0:
                return search(uri.getLastPathSegment());
            default:
                throw new IllegalArgumentException("Uri: " + uri);
        }
    }

    private Cursor search(String query) {
        if (query == null || query.equals(SearchManager.SUGGEST_URI_PATH_QUERY))
            query = "";

        return repository.search(getContext().getPackageName(), query);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        initialise();
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        initialise();
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        initialise();
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        initialise();
        return 0;
    }

    @Inject
    void setRepository(SearchRepositoryImpl repository) {
        this.repository = repository;
    }
}
