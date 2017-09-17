package de.njsm.stocks.backend.db;

import android.content.ContentValues;
import android.content.ContextWrapper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.backend.db.data.*;

public class DatabaseHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 16;

    private static final String DATABASE_NAME = "stocks.db";

    public DatabaseHandler(ContextWrapper context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SqlUserTable.CREATE);
            db.execSQL(SqlUpdateTable.CREATE);
            db.execSQL(SqlUpdateTable.INIT);
            db.execSQL(SqlDeviceTable.CREATE);
            db.execSQL(SqlLocationTable.CREATE);
            db.execSQL(SqlFoodTable.CREATE);
            db.execSQL(SqlFoodItemTable.CREATE);
        } catch (SQLException e) {
            Log.e(Config.LOG_TAG, "could not create table", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL(SqlUserTable.DROP);
            db.execSQL(SqlUpdateTable.DROP);
            db.execSQL(SqlDeviceTable.DROP);
            db.execSQL(SqlLocationTable.DROP);
            db.execSQL(SqlFoodTable.DROP);
            db.execSQL(SqlFoodItemTable.DROP);
        } catch (SQLException e) {
            Log.e(Config.LOG_TAG, "could not drop tables", e);
        }

        onCreate(db);
    }

    void writeData(String tableName, ContentValues[] values) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM " + tableName);
            for (ContentValues u : values) {
                db.insertOrThrow(tableName, null, u);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Config.LOG_TAG, "could not write table" + tableName, e);
        } finally {
            db.endTransaction();
        }
    }
}
