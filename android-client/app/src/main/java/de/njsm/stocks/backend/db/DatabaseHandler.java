package de.njsm.stocks.backend.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.njsm.stocks.Config;
import de.njsm.stocks.backend.data.Food;
import de.njsm.stocks.backend.data.FoodItem;
import de.njsm.stocks.backend.data.Location;
import de.njsm.stocks.backend.data.Update;
import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.data.UserDevice;
import de.njsm.stocks.backend.db.data.SqlDeviceTable;
import de.njsm.stocks.backend.db.data.SqlFoodItemTable;
import de.njsm.stocks.backend.db.data.SqlFoodTable;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.db.data.SqlUpdateTable;
import de.njsm.stocks.backend.db.data.SqlUserTable;
import de.njsm.stocks.backend.network.SyncTask;

public class DatabaseHandler extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 14;
    public static final String DATABASE_NAME = "stocks.db";

    public static DatabaseHandler h;

    protected Context mContext;

    public static void init(Context c) {
        if (h == null) {
            h = new DatabaseHandler(c);
        }
    }

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

    public Update[] getUpdates() {
        Cursor c = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

        try {
            SQLiteDatabase db = getReadableDatabase();
            c = db.rawQuery(SqlUpdateTable.SELECT_ALL, null);
            ArrayList<Update> result = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                Date date = format.parse(c.getString(c.getColumnIndex(SqlUpdateTable.COL_DATE)));
                Update u = new Update(c.getString(c.getColumnIndex(SqlUpdateTable.COL_NAME)),
                        date);
                result.add(u);
                c.moveToNext();
            }
            return result.toArray(new Update[result.size()]);

        } catch (SQLException e) {
            Log.e(Config.log, "could not get updates", e);
            return new Update[0];
        } catch (ParseException e) {
            Log.e(Config.log, "could not parse date", e);
            return new Update[0];
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public void writeUpdates(Update[] updates) {
        SQLiteDatabase db = getWritableDatabase();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

        try {
            db.beginTransaction();
            db.execSQL(SqlUpdateTable.CLEAR);
            int i = 1;
            for (Update u : updates) {
                ContentValues v = new ContentValues();
                v.put(SqlUpdateTable.COL_ID, i);
                v.put(SqlUpdateTable.COL_NAME, u.table);
                v.put(SqlUpdateTable.COL_DATE, format.format(u.lastUpdate));
                db.insertOrThrow(SqlUpdateTable.NAME, null, v);
                i++;
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

    public Food[] getFood() {
        Cursor c = null;

        try {
            SQLiteDatabase db = getReadableDatabase();
            c = db.rawQuery(SqlFoodTable.SELECT_ALL, null);
            ArrayList<Food> result = new ArrayList<>();

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Food u = new Food(
                        c.getInt(c.getColumnIndex(SqlFoodTable.COL_ID)),
                        c.getString(c.getColumnIndex(SqlFoodTable.COL_NAME)));
                result.add(u);
                c.moveToNext();
            }
            return result.toArray(new Food[result.size()]);

        } catch (SQLException e) {
            Log.e(Config.log, "could not get food", e);
            return new Food[0];
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public void writeFood(Food[] food) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.execSQL(SqlFoodTable.CLEAR);

            for (Food u : food) {
                ContentValues v = new ContentValues();
                v.put(SqlFoodTable.COL_ID, u.id);
                v.put(SqlFoodTable.COL_NAME, u.name);
                db.insertOrThrow(SqlFoodTable.NAME, null, v);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Config.log, "could not write food", e);
        } finally {
            db.endTransaction();
        }
    }

    public FoodItem[] getItems() {
        Cursor c = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

        try {
            SQLiteDatabase db = getReadableDatabase();
            c = db.rawQuery(SqlFoodItemTable.SELECT_ALL, null);
            ArrayList<FoodItem> result = new ArrayList<>();

            c.moveToFirst();
            while (!c.isAfterLast()) {
                FoodItem u = new FoodItem(
                        c.getInt(c.getColumnIndex(SqlFoodItemTable.COL_ID)),
                        format.parse(c.getString(c.getColumnIndex(SqlFoodItemTable.COL_EAT_BY))),
                        c.getInt(c.getColumnIndex(SqlFoodItemTable.COL_OF_TYPE)),
                        c.getInt(c.getColumnIndex(SqlFoodItemTable.COL_STORED_IN)),
                        c.getInt(c.getColumnIndex(SqlFoodItemTable.COL_REGISTERS)),
                        c.getInt(c.getColumnIndex(SqlFoodItemTable.COL_BUYS)));
                result.add(u);
                c.moveToNext();
            }
            return result.toArray(new FoodItem[result.size()]);

        } catch (SQLException e) {
            Log.e(Config.log, "could not get items", e);
            return new FoodItem[0];
        } catch (ParseException e) {
            e.printStackTrace();
            return new FoodItem[0];
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public void writeItems(FoodItem[] items) {
        SQLiteDatabase db = getWritableDatabase();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

        try {
            db.beginTransaction();
            db.execSQL(SqlFoodItemTable.CLEAR);

            for (FoodItem u : items) {
                ContentValues v = new ContentValues();
                v.put(SqlFoodItemTable.COL_ID, u.id);
                v.put(SqlFoodItemTable.COL_EAT_BY, format.format(u.eatByDate));
                v.put(SqlFoodItemTable.COL_BUYS, u.buys);
                v.put(SqlFoodItemTable.COL_OF_TYPE, u.ofType);
                v.put(SqlFoodItemTable.COL_REGISTERS, u.registers);
                v.put(SqlFoodItemTable.COL_STORED_IN, u.storedIn);
                db.insertOrThrow(SqlFoodItemTable.NAME, null, v);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Config.log, "could not write items", e);
        } finally {
            db.endTransaction();
        }
    }

}
