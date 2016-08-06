package de.njsm.stocks.backend.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.njsm.stocks.Config;
import de.njsm.stocks.backend.db.data.SqlDeviceTable;
import de.njsm.stocks.backend.db.data.SqlFoodItemTable;
import de.njsm.stocks.backend.db.data.SqlFoodTable;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.db.data.SqlUpdateTable;
import de.njsm.stocks.backend.db.data.SqlUserTable;
import de.njsm.stocks.backend.network.SyncTask;

public class DatabaseHandler extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 16;
    public static final String DATABASE_NAME = "stocks.db";

    protected Context mContext;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
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
            SyncTask task = new SyncTask(mContext);
            task.execute();
        } catch (SQLException e) {
            Log.e(Config.log, "could not create table", e);
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
            Log.e(Config.log, "could not drop tables", e);
        }

        onCreate(db);
    }

    public void writeUpdates(ContentValues[] updates) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.execSQL(SqlUpdateTable.CLEAR);
            for (ContentValues u : updates) {
                db.insertOrThrow(SqlUpdateTable.NAME, null, u);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Config.log, "could not write updates", e);
        } finally {
            db.endTransaction();
        }
    }

    public void writeUsers(ContentValues[] users) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.execSQL(SqlUserTable.CLEAR);

            for (ContentValues u : users) {
                db.insertOrThrow(SqlUserTable.NAME, null, u);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Config.log, "could not write users", e);
        } finally {
            db.endTransaction();
        }
    }

    public void writeDevices(ContentValues[] devs) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.execSQL(SqlDeviceTable.CLEAR);

            for (ContentValues u : devs) {
                db.insertOrThrow(SqlDeviceTable.NAME, null, u);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Config.log, "could not write devices", e);
        } finally {
            db.endTransaction();
        }
    }

    public void writeLocations(ContentValues[] values) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.execSQL(SqlLocationTable.CLEAR);

            for (ContentValues u : values) {
                db.insertOrThrow(SqlLocationTable.NAME, null, u);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Config.log, "could not write locations", e);
        } finally {
            db.endTransaction();
        }
    }

    public void writeFood(ContentValues[] food) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.execSQL(SqlFoodTable.CLEAR);

            for (ContentValues u : food) {
                db.insertOrThrow(SqlFoodTable.NAME, null, u);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Config.log, "could not write food", e);
        } finally {
            db.endTransaction();
        }
    }

    public void writeItems(ContentValues[] items) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.execSQL(SqlFoodItemTable.CLEAR);

            for (ContentValues u : items) {
                db.insertOrThrow(SqlFoodItemTable.NAME, null, u);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Config.log, "could not write items", e);
        } finally {
            db.endTransaction();
        }
    }

}
