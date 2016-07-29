package de.njsm.stocks.backend.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
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
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlDeviceTable;
import de.njsm.stocks.backend.db.data.SqlFoodItemTable;
import de.njsm.stocks.backend.db.data.SqlFoodTable;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.db.data.SqlUpdateTable;
import de.njsm.stocks.backend.db.data.SqlUserTable;

public class SyncTask extends AsyncTask<Void, Void, Integer> {

    protected Context c;
    protected AsyncTaskCallback mListener;

    public SyncTask(Context c) {
        this.c = c;
    }

    public SyncTask(Context c,
                    AsyncTaskCallback listener) {
        this.c = c;
        this.mListener = listener;
    }

    @Override
    protected Integer doInBackground(Void... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        Update[] serverUpdates = ServerManager.m.getUpdates();
        Update[] localUpdates = getUpdates();

        if (serverUpdates.length == 0 ||
                localUpdates.length == 0) {
            Log.e(Config.log, "Array is empty " + serverUpdates.length + ", " +
                localUpdates.length);
            return 0;
        }

        for (int i = 0; i < serverUpdates.length; i++) {
            if (serverUpdates[i].lastUpdate.after(localUpdates[i].lastUpdate)) {
                refresh(serverUpdates[i].table);
            }
        }

        writeUpdates(serverUpdates);
        Log.i(Config.log, "Sync successful");
        return 0;
    }

    protected void refresh(String table) {
        if (table.equals(SqlUserTable.NAME)) {
            refreshUsers();
        } else if (table.equals(SqlDeviceTable.NAME)) {
            refreshDevices();
        } else if (table.equals(SqlLocationTable.NAME)) {
            refreshLocations();
        } else if (table.equals(SqlFoodTable.NAME)) {
            refreshFood();
        } else if (table.equals(SqlFoodItemTable.NAME)) {
            refreshItems();
        }
    }

    protected void refreshFood() {
        Food[] u = ServerManager.m.getFood();

        ContentValues[] values = new ContentValues[u.length];
        for (int i = 0; i < u.length; i++) {
            values[i] = new ContentValues();
            values[i].put(SqlFoodTable.COL_ID, u[i].id);
            values[i].put(SqlFoodTable.COL_NAME, u[i].name);
        }

        c.getContentResolver().bulkInsert(
                Uri.withAppendedPath(StocksContentProvider.baseUri, SqlFoodTable.NAME),
                values);
    }

    protected void refreshItems() {
        FoodItem[] u = ServerManager.m.getFoodItems();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

        ContentValues[] values = new ContentValues[u.length];
        for (int i = 0; i < u.length; i++) {
            values[i] = new ContentValues();
            values[i].put(SqlFoodItemTable.COL_ID, u[i].id);
            values[i].put(SqlFoodItemTable.COL_REGISTERS, u[i].registers);
            values[i].put(SqlFoodItemTable.COL_BUYS, u[i].buys);
            values[i].put(SqlFoodItemTable.COL_OF_TYPE, u[i].ofType);
            values[i].put(SqlFoodItemTable.COL_STORED_IN, u[i].storedIn);
            values[i].put(SqlFoodItemTable.COL_EAT_BY, format.format(u[i].eatByDate));

        }

        c.getContentResolver().bulkInsert(
                Uri.withAppendedPath(StocksContentProvider.baseUri, SqlFoodItemTable.NAME),
                values);
    }

    protected void refreshLocations() {
        Location[] u = ServerManager.m.getLocations();

        ContentValues[] values = new ContentValues[u.length];
        for (int i = 0; i < u.length; i++) {
            values[i] = new ContentValues();
            values[i].put(SqlLocationTable.COL_ID, u[i].id);
            values[i].put(SqlLocationTable.COL_NAME, u[i].name);
        }

        c.getContentResolver().bulkInsert(
                Uri.withAppendedPath(StocksContentProvider.baseUri, SqlLocationTable.NAME),
                values);
    }

    protected void refreshUsers() {
        User[] u = ServerManager.m.getUsers();

        ContentValues[] values = new ContentValues[u.length];
        for (int i = 0; i < u.length; i++) {
            values[i] = new ContentValues();
            values[i].put(SqlUserTable.COL_ID, u[i].id);
            values[i].put(SqlUserTable.COL_NAME, u[i].name);
        }

        c.getContentResolver().bulkInsert(
                Uri.withAppendedPath(StocksContentProvider.baseUri, SqlUserTable.NAME),
                values);

    }

    protected void refreshDevices() {
        UserDevice[] u = ServerManager.m.getDevices();

        ContentValues[] values = new ContentValues[u.length];
        for (int i = 0; i < u.length; i++) {
            values[i] = new ContentValues();
            values[i].put(SqlDeviceTable.COL_ID, u[i].id);
            values[i].put(SqlDeviceTable.COL_NAME, u[i].name);
            values[i].put(SqlDeviceTable.COL_USER, u[i].userId);
        }

        c.getContentResolver().bulkInsert(
                Uri.withAppendedPath(StocksContentProvider.baseUri, SqlDeviceTable.NAME),
                values);
    }

    public Update[] getUpdates() {
        Cursor cursor = c.getContentResolver().query(
                Uri.withAppendedPath(StocksContentProvider.baseUri, SqlUpdateTable.NAME),
                null,
                null,
                null,
                null,
                null
        );
        assert cursor != null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

        ArrayList<Update> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Date date = null;
            try {
                date = format.parse(cursor.getString(cursor.getColumnIndex(SqlUpdateTable.COL_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Update u = new Update(cursor.getString(cursor.getColumnIndex(SqlUpdateTable.COL_NAME)),
                    date);
            result.add(u);
            cursor.moveToNext();
        }
        cursor.close();
        return result.toArray(new Update[result.size()]);

    }

    private void writeUpdates(Update[] serverUpdates) {
        ContentValues[] values = new ContentValues[serverUpdates.length];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

        int i = 0;
        for (Update u : serverUpdates) {
            ContentValues v = new ContentValues();
            v.put(SqlUpdateTable.COL_ID, i);
            v.put(SqlUpdateTable.COL_NAME, u.table);
            v.put(SqlUpdateTable.COL_DATE, format.format(u.lastUpdate));
            values[i] = v;
            i++;
        }

        c.getContentResolver().bulkInsert(
                Uri.withAppendedPath(StocksContentProvider.baseUri, SqlUpdateTable.NAME),
                values);
    }

    public void registerListener(@Nullable AsyncTaskCallback l) {
        mListener = l;
    }

    @Override
    protected void onPreExecute() {

        if (mListener != null) {
            mListener.onAsyncTaskStart();
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if (mListener != null) {
            mListener.onAsyncTaskComplete();
        }
    }
}

