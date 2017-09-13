package de.njsm.stocks.backend.db;

import android.content.ContentValues;
import android.content.ContextWrapper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.njsm.stocks.Config;
import de.njsm.stocks.backend.db.data.*;
import de.njsm.stocks.backend.network.NetworkManager;

public class DatabaseHandler extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 16;
    public static final String DATABASE_NAME = "stocks.db";

    protected ContextWrapper mContext;

    private NetworkManager networkManager;

    public DatabaseHandler(ContextWrapper context, NetworkManager networkManager) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        this.networkManager = networkManager;
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
            networkManager.synchroniseData();
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
            Log.e(Config.LOG_TAG, "could not write updates", e);
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
            Log.e(Config.LOG_TAG, "could not write users", e);
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
            Log.e(Config.LOG_TAG, "could not write devices", e);
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
            Log.e(Config.LOG_TAG, "could not write locations", e);
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
            Log.e(Config.LOG_TAG, "could not write food", e);
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
            Log.e(Config.LOG_TAG, "could not write items", e);
        } finally {
            db.endTransaction();
        }
    }

}
