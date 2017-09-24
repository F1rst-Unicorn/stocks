package de.njsm.stocks.backend.db;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import de.njsm.stocks.backend.db.data.*;

public class StocksContentProvider extends ContentProvider {

    public static final String AUTHORITY = "de.njsm.stocks.providers.StocksContentProvider";

    public static final Uri BASE_URI = Uri.parse("content://de.njsm.stocks.providers.StocksContentProvider");

    public static final String FOOD_ITEM_LOCATION = "Food_item/by_location";

    public static final String FOOD_ITEM_TYPE = "Food_item/by_food_type";

    public static final String EMPTY_FOOD = "Food/empty";

    public static final String EAT_SOON = "Food/eat_soon";

    public static final String MAX_LOCATION = "Location/eat_soon";

    public static final String SEARCH_FOOD = "Search/food";

    private UriMatcher sMatcher;

    private DatabaseHandler mHandler;

    @Override
    public boolean onCreate() {
        mHandler = new DatabaseHandler(new ContextWrapper(getContext()));
        initialiseUriMatcher();
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
        SQLiteDatabase db = mHandler.getReadableDatabase();

        switch (sMatcher.match(uri)) {
            case 0:
                result = db.rawQuery(SqlUserTable.SELECT_ALL, null);
                break;
            case 1:
                if (selectionArgs != null && selectionArgs.length == 1) {
                    result = db.rawQuery(SqlDeviceTable.SELECT_USER, selectionArgs);
                } else {
                    result = db.rawQuery(SqlDeviceTable.SELECT_ALL, null);
                }
                break;
            case 2:
                result = db.rawQuery(SqlLocationTable.SELECT_ALL, null);
                break;
            case 5:
                result = db.rawQuery(SqlUpdateTable.SELECT_ALL, null);
                break;
            case 6:
                result = db.rawQuery(SqlFoodItemTable.SELECT_AGGREGATED_MIN_DATE_LOC, selectionArgs);
                break;
            case 7:
                result = db.rawQuery(SqlFoodItemTable.SELECT_FOOD_TYPE_ALL, selectionArgs);
                break;
            case 8:
                result = db.rawQuery(SqlFoodItemTable.SELECT_FOOD_EMPTY, null);
                break;
            case 9:
                result = db.rawQuery(SqlFoodItemTable.SELECT_FOOD_EAT_SOON, null);
                break;
            case 10:
                result = db.rawQuery(SqlFoodItemTable.SELECT_MAX_LOCATION, selectionArgs);
                break;
            case 11:
                result = db.rawQuery(SqlFoodItemTable.SEARCH_FOOD, selectionArgs);
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
        int match = sMatcher.match(uri);
        ContentResolver resolver = getContext().getContentResolver();
        Uri uri6 = Uri.withAppendedPath(BASE_URI, FOOD_ITEM_LOCATION);
        Uri uri7 = Uri.withAppendedPath(BASE_URI, FOOD_ITEM_TYPE);
        Uri uri8 = Uri.withAppendedPath(BASE_URI, EMPTY_FOOD);
        Uri uri9 = Uri.withAppendedPath(BASE_URI, EAT_SOON);
        Uri uri11 = Uri.withAppendedPath(BASE_URI, SEARCH_FOOD);


        switch (match) {
            case 0:
                mHandler.writeData(SqlUserTable.NAME, values);
                resolver.notifyChange(uri7, null);
                break;
            case 1:
                mHandler.writeData(SqlDeviceTable.NAME, values);
                resolver.notifyChange(uri7, null);
                break;
            case 2:
                mHandler.writeData(SqlLocationTable.NAME, values);
                resolver.notifyChange(uri7, null);
                break;
            case 3:
                mHandler.writeData(SqlFoodTable.NAME, values);
                resolver.notifyChange(uri6, null);
                resolver.notifyChange(uri7, null);
                resolver.notifyChange(uri8, null);
                resolver.notifyChange(uri9, null);
                resolver.notifyChange(uri11, null);
                break;
            case 4:
                mHandler.writeData(SqlFoodItemTable.NAME, values);
                resolver.notifyChange(uri6, null);
                resolver.notifyChange(uri8, null);
                resolver.notifyChange(uri9, null);
                resolver.notifyChange(uri11, null);
                break;
            case 5:
                mHandler.writeData(SqlUpdateTable.NAME, values);
                break;
            default:
                throw new IllegalArgumentException("Uri: " + uri.toString());
        }
        resolver.notifyChange(uri, null);
        return values.length;
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

    private void initialiseUriMatcher() {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sMatcher.addURI(AUTHORITY, SqlUserTable.NAME, 0);
        sMatcher.addURI(AUTHORITY, SqlDeviceTable.NAME, 1);
        sMatcher.addURI(AUTHORITY, SqlLocationTable.NAME, 2);
        sMatcher.addURI(AUTHORITY, SqlFoodTable.NAME, 3);
        sMatcher.addURI(AUTHORITY, SqlFoodItemTable.NAME, 4);
        sMatcher.addURI(AUTHORITY, SqlUpdateTable.NAME, 5);

        sMatcher.addURI(AUTHORITY, FOOD_ITEM_LOCATION, 6);
        sMatcher.addURI(AUTHORITY, FOOD_ITEM_TYPE, 7);
        sMatcher.addURI(AUTHORITY, EMPTY_FOOD, 8);
        sMatcher.addURI(AUTHORITY, EAT_SOON, 9);
        sMatcher.addURI(AUTHORITY, MAX_LOCATION, 10);
        sMatcher.addURI(AUTHORITY, SEARCH_FOOD, 11);
    }
}
