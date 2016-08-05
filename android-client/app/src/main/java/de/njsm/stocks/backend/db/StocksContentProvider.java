package de.njsm.stocks.backend.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.njsm.stocks.backend.db.data.SqlDeviceTable;
import de.njsm.stocks.backend.db.data.SqlFoodItemTable;
import de.njsm.stocks.backend.db.data.SqlFoodTable;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.db.data.SqlUpdateTable;
import de.njsm.stocks.backend.db.data.SqlUserTable;

public class StocksContentProvider extends ContentProvider {

    public static final String AUTHORITY = "de.njsm.stocks.providers.StocksContentProvider";
    public static final Uri baseUri = Uri.parse("content://de.njsm.stocks.providers.StocksContentProvider");

    public static final String foodItemLocation = "Food_item/by_location";
    public static final String foodItemType = "Food_item/by_food_type";
    public static final String emptyFood = "Food/empty";
    public static final String eatSoon = "Food/eat_soon";

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
        SQLiteDatabase db = mHandler.getReadableDatabase();

        switch (sMatcher.match(uri)) {
            case 0:
                result = db.rawQuery(SqlUserTable.SELECT_ALL, null);
                break;
            case 1:
                if (selectionArgs != null &&
                        selectionArgs.length == 1) {
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
        final ContentResolver resolver = getContext().getContentResolver();
        Uri uri6 = Uri.withAppendedPath(baseUri, foodItemLocation);
        Uri uri7 = Uri.withAppendedPath(baseUri, foodItemType);
        Uri uri8 = Uri.withAppendedPath(baseUri, emptyFood);
        Uri uri9 = Uri.withAppendedPath(baseUri, eatSoon);


        switch (match) {
            case 0:
                mHandler.writeUsers(values);
                resolver.notifyChange(uri7, null);
                break;
            case 1:
                mHandler.writeDevices(values);
                resolver.notifyChange(uri7, null);
                break;
            case 2:
                mHandler.writeLocations(values);
                resolver.notifyChange(uri7, null);
                break;
            case 3:
                mHandler.writeFood(values);
                resolver.notifyChange(uri6, null);
                resolver.notifyChange(uri7, null);
                resolver.notifyChange(uri8, null);
                resolver.notifyChange(uri9, null);
                break;
            case 4:
                mHandler.writeItems(values);
                resolver.notifyChange(uri6, null);
                resolver.notifyChange(uri8, null);
                resolver.notifyChange(uri9, null);
                break;
            case 5:
                mHandler.writeUpdates(values);
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

    static {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sMatcher.addURI(AUTHORITY, SqlUserTable.NAME, 0);
        sMatcher.addURI(AUTHORITY, SqlDeviceTable.NAME, 1);
        sMatcher.addURI(AUTHORITY, SqlLocationTable.NAME, 2);
        sMatcher.addURI(AUTHORITY, SqlFoodTable.NAME, 3);
        sMatcher.addURI(AUTHORITY, SqlFoodItemTable.NAME, 4);
        sMatcher.addURI(AUTHORITY, SqlUpdateTable.NAME, 5);

        sMatcher.addURI(AUTHORITY, foodItemLocation, 6);
        sMatcher.addURI(AUTHORITY, foodItemType, 7);
        sMatcher.addURI(AUTHORITY, emptyFood, 8);
        sMatcher.addURI(AUTHORITY, eatSoon, 9);
    }
}
