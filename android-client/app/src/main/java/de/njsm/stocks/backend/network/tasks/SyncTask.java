package de.njsm.stocks.backend.network.tasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import de.njsm.stocks.Config;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.*;
import de.njsm.stocks.backend.network.AsyncTaskCallback;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.common.data.*;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncTask extends AbstractNetworkTask<Void, Void, Integer> {

    private static AtomicBoolean sRunning = new AtomicBoolean(false);

    private ContentResolver resolver;

    private AsyncTaskCallback listener;

    public SyncTask(File exceptionFileDirectory,
                    ServerManager serverManager,
                    ContentResolver resolver,
                    AsyncTaskCallback listener) {
        super(exceptionFileDirectory, serverManager);
        this.resolver = resolver;
        this.listener = listener;
    }

    @Override
    protected Integer doInBackgroundInternally(Void... params) {

        if (!sRunning.compareAndSet(false, true)){
            Log.i(Config.LOG_TAG, "Another sync task is already running");
            return 0;
        }

        Update[] serverUpdates = serverManager.getUpdates();
        Update[] localUpdates = getLocalUpdates();

        if (updateOutdatedTables(serverUpdates, localUpdates)) {
            return 0;
        }

        writeUpdates(serverUpdates);

        Log.i(Config.LOG_TAG, "Sync successful");

        sRunning.set(false);
        return 0;
    }

    protected boolean updateOutdatedTables(Update[] serverUpdates, Update[] localUpdates) {
        if (serverUpdates.length == 0) {
            Log.e(Config.LOG_TAG, "Array is empty " + serverUpdates.length + ", " +
                localUpdates.length);
            return true;
        }
        if (localUpdates.length == 0) {
            refreshAll();
        } else {
            for (Update update : serverUpdates) {
                Update localUpdate = getLocalUpdate(localUpdates, update.table);
                if (localUpdate != null && update.lastUpdate.after(localUpdate.lastUpdate)) {
                    refresh(update.table);
                }
            }
        }
        return false;
    }

    protected Update getLocalUpdate(Update[] localUpdates, String table) {
        for (Update u : localUpdates) {
            if (u.table.equals(table)) {
                return u;
            }
        }
        return null;
    }

    private void refreshAll() {
        refreshUsers();
        refreshDevices();
        refreshLocations();
        refreshFood();
        refreshItems();
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
        Food[] u = serverManager.getFood();

        ContentValues[] values = new ContentValues[u.length];
        for (int i = 0; i < u.length; i++) {
            values[i] = new ContentValues();
            values[i].put(SqlFoodTable.COL_ID, u[i].id);
            values[i].put(SqlFoodTable.COL_NAME, u[i].name);
        }

        resolver.bulkInsert(Uri.withAppendedPath(StocksContentProvider.baseUri, SqlFoodTable.NAME),
                values);
    }

    protected void refreshItems() {
        FoodItem[] u = serverManager.getFoodItems();
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

        resolver.bulkInsert(Uri.withAppendedPath(StocksContentProvider.baseUri, SqlFoodItemTable.NAME),
                values);
    }

    protected void refreshLocations() {
        Location[] u = serverManager.getLocations();

        ContentValues[] values = new ContentValues[u.length];
        for (int i = 0; i < u.length; i++) {
            values[i] = new ContentValues();
            values[i].put(SqlLocationTable.COL_ID, u[i].id);
            values[i].put(SqlLocationTable.COL_NAME, u[i].name);
        }

        resolver.bulkInsert(Uri.withAppendedPath(StocksContentProvider.baseUri, SqlLocationTable.NAME),
                values);
    }

    protected void refreshUsers() {
        User[] u = serverManager.getUsers();

        ContentValues[] values = new ContentValues[u.length];
        for (int i = 0; i < u.length; i++) {
            values[i] = new ContentValues();
            values[i].put(SqlUserTable.COL_ID, u[i].id);
            values[i].put(SqlUserTable.COL_NAME, u[i].name);
        }

        resolver.bulkInsert(Uri.withAppendedPath(StocksContentProvider.baseUri, SqlUserTable.NAME),
                values);

    }

    protected void refreshDevices() {
        UserDevice[] u = serverManager.getDevices();

        ContentValues[] values = new ContentValues[u.length];
        for (int i = 0; i < u.length; i++) {
            values[i] = new ContentValues();
            values[i].put(SqlDeviceTable.COL_ID, u[i].id);
            values[i].put(SqlDeviceTable.COL_NAME, u[i].name);
            values[i].put(SqlDeviceTable.COL_USER, u[i].userId);
        }

        resolver.bulkInsert(Uri.withAppendedPath(StocksContentProvider.baseUri, SqlDeviceTable.NAME),
                values);
    }

    public Update[] getLocalUpdates() {
        Cursor cursor = resolver.query(
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

        resolver.bulkInsert(Uri.withAppendedPath(StocksContentProvider.baseUri, SqlUpdateTable.NAME),
                values);
    }

    @Override
    protected void onPreExecute() {
        if (listener != null) {
            listener.onAsyncTaskStart();
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if (listener != null) {
            listener.onAsyncTaskComplete();
        }
    }
}

