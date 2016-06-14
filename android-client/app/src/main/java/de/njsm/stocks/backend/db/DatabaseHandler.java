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

import de.njsm.stocks.Config;
import de.njsm.stocks.backend.data.Update;
import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.db.data.SqlUpdateTable;
import de.njsm.stocks.backend.db.data.SqlUserTable;

public class DatabaseHandler extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "stocks.db";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SqlUserTable.CREATE);
            db.execSQL(SqlUpdateTable.CREATE);
            db.execSQL(SqlUpdateTable.INIT);
        } catch (SQLException e) {
            Log.e(Config.log, "could not create table", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL(SqlUserTable.DROP);
            db.execSQL(SqlUpdateTable.DROP);
        } catch (SQLException e) {
            Log.e(Config.log, "could not drop tables", e);
        }

        onCreate(db);
    }

    public Update[] getUpdates() {
        Cursor c = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            SQLiteDatabase db = getReadableDatabase();
            c = db.rawQuery(SqlUpdateTable.SELECT_ALL, null);
            ArrayList<Update> result = new ArrayList<>();

            while (!c.isAfterLast()) {
                Date date = format.parse(c.getString(c.getColumnIndex(SqlUpdateTable.COL_DATE)));
                Update u = new Update(c.getString(c.getColumnIndex(SqlUpdateTable.COL_NAME)),
                        date);
                result.add(u);
            }
            return result.toArray(new Update[result.size()]);

        } catch (SQLException e) {
            Log.e(Config.log, "could not get users", e);
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

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
            Log.e(Config.log, "could not get users", e);
        } finally {
            db.endTransaction();
        }
    }

    public User[] getUsers() {
        Cursor c = null;

        try {
            SQLiteDatabase db = getReadableDatabase();
            c = db.rawQuery(SqlUserTable.SELECT_ALL, null);
            ArrayList<User> result = new ArrayList<>();

            while (!c.isAfterLast()) {
                User u = new User(
                        c.getInt(c.getColumnIndex(SqlUserTable.COL_ID)),
                        c.getString(c.getColumnIndex(SqlUserTable.COL_NAME)));
                result.add(u);
            }
            return result.toArray(new User[result.size()]);

        } catch (SQLException e) {
            Log.e(Config.log, "could not get users", e);
            return new User[0];
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public User[] getUsers(String name) {
        Cursor c = null;

        try {
            SQLiteDatabase db = getReadableDatabase();
            c = db.rawQuery(SqlUserTable.SELECT_NAME, new String[] {name});
            ArrayList<User> result = new ArrayList<>();

            while (!c.isAfterLast()) {
                User u = new User(
                        c.getInt(c.getColumnIndex(SqlUserTable.COL_ID)),
                        c.getString(c.getColumnIndex(SqlUserTable.COL_NAME)));
                result.add(u);
            }
            return result.toArray(new User[result.size()]);

        } catch (SQLException e) {
            Log.e(Config.log, "could not get users", e);
            return new User[0];
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public void writeUsers(User[] users) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.execSQL(SqlUserTable.CLEAR);

            for (User u : users) {
                ContentValues v = new ContentValues();
                v.put(SqlUserTable.COL_ID, u.id);
                v.put(SqlUserTable.COL_NAME, u.name);
                db.insertOrThrow(SqlUserTable.NAME, null, v);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Config.log, "could not get users", e);
        } finally {
            db.endTransaction();
        }
    }
}
