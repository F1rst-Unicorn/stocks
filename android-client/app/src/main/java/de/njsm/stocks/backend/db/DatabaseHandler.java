package de.njsm.stocks.backend.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.db.data.SqlUserTable;

public class DatabaseHandler extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "stocks.db";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SqlUserTable.CREATE);
        } catch (SQLException e) {
            Log.e("stocks", "could not create table", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL(SqlUserTable.CLEAR);
        } catch (SQLException e) {
            Log.e("stocks", "could not drop tables", e);
        }

        onCreate(db);
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
            Log.e("stocks", "could not get users", e);
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
            Log.e("stocks", "could not get users", e);
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

            for (User u : users) {
                ContentValues v = new ContentValues();
                v.put(SqlUserTable.COL_ID, u.id);
                v.put(SqlUserTable.COL_NAME, u.name);
                db.insertOrThrow(SqlUserTable.NAME, null, v);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("stocks", "could not get users", e);
        } finally {
            db.endTransaction();
        }
    }
}
