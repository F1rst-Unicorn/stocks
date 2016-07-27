package de.njsm.stocks.backend.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.njsm.stocks.backend.db.data.SqlUserTable;

public class StocksContentProvider extends ContentProvider {

    public static final String AUTHORITY = "de.njsm.stocks.providers.StocksContentProvider";

    public static final Uri baseUri = Uri.parse("content://de.njsm.stocks.providers.StocksContentProvider");

    private static final UriMatcher sMatcher;

    protected DatabaseHandler mHandler;

    @Override
    public boolean onCreate() {
        mHandler = new DatabaseHandler(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        Cursor result;

        switch (sMatcher.match(uri)) {
            case 0:
                SQLiteDatabase db = mHandler.getReadableDatabase();
                result = db.rawQuery(SqlUserTable.SELECT_ALL, null);
                break;
            default:
                throw new IllegalArgumentException("Uri: " + uri.toString());
        }

        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri,
                          @NonNull ContentValues[] values) {
        final int match = sMatcher.match(uri);
        int result;

        switch (match) {
            case 0:
                mHandler.writeUsers(values);
                result = values.length;
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new IllegalArgumentException("Uri: " + uri.toString());
        }

        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    static {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sMatcher.addURI(AUTHORITY, SqlUserTable.NAME, 0);
    }
}
