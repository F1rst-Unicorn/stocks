package de.njsm.stocks.backend.network.tasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.backend.data.SerialisationVisitor;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.*;
import de.njsm.stocks.backend.network.AsyncTaskCallback;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.common.data.*;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncTask extends AbstractNetworkTask<Void, Void, Integer> {

    private static AtomicBoolean sRunning = new AtomicBoolean(false);

    private ContentResolver resolver;

    private AsyncTaskCallback listener;

    private SerialisationVisitor serialiser;

    public SyncTask(File exceptionFileDirectory,
                    ServerManager serverManager,
                    ContentResolver resolver,
                    AsyncTaskCallback listener) {
        super(exceptionFileDirectory, serverManager);
        this.resolver = resolver;
        this.listener = listener;
        this.serialiser = new SerialisationVisitor();
    }

    @Override
    protected Integer doInBackgroundInternally(Void... params) {

        if (!sRunning.compareAndSet(false, true)){
            Log.i(Config.LOG_TAG, "Another sync task is already running");
            return 0;
        }

        Update[] serverUpdates = serverManager.getUpdates();
        Update[] localUpdates = readLocalUpdates();
        updateOutdatedTables(serverUpdates, localUpdates);

        sRunning.set(false);
        return 0;
    }

    void updateOutdatedTables(Update[] serverUpdates, Update[] localUpdates) {
        if (serverUpdates.length == 0) {
            Log.e(Config.LOG_TAG, "Server updates are empty");
        } else if (localUpdates.length == 0) {
            refreshAll();
        } else {
            refreshOutdatedTables(serverUpdates, localUpdates);
        }
        writeUpdates(serverUpdates);
    }

    private void refreshOutdatedTables(Update[] serverUpdates, Update[] localUpdates) {
        for (Update update : serverUpdates) {
            if (isTableOutdated(localUpdates, update)) {
                refresh(update.table);
            }
        }
    }

    private boolean isTableOutdated(Update[] localUpdates, Update update) {
        Update localUpdate = getLocalUpdate(localUpdates, update.table);
        return localUpdate != null && update.lastUpdate.after(localUpdate.lastUpdate);
    }

    private Update getLocalUpdate(Update[] localUpdates, String table) {
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

    private void refresh(String table) {
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
        } else if (table.equals(SqlEanNumberTable.NAME)) {
            refreshEanNumbers();
        }
    }

    private void refreshEanNumbers() {
        EanNumber[] n = serverManager.getEanNumbers();
        writeEanNumbers(n);
    }

    private void writeEanNumbers(EanNumber[] numbers) {
        ContentValues[] values = new ContentValues[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            values[i] = serialiser.visit(numbers[i], 0);
        }

        resolver.bulkInsert(Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlEanNumberTable.NAME),
                values);
    }

    private void refreshFood() {
        Food[] u = serverManager.getFood();
        writeFood(u);
    }

    private void writeFood(Food[] u) {
        ContentValues[] values = new ContentValues[u.length];
        for (int i = 0; i < u.length; i++) {
            values[i] = serialiser.visit(u[i], 0);
        }

        resolver.bulkInsert(
                Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlFoodTable.NAME),
                values);
    }

    private void refreshItems() {
        FoodItem[] items = serverManager.getFoodItems();
        writeItems(items);
    }

    private void writeItems(FoodItem[] items) {
        ContentValues[] values = new ContentValues[items.length];
        for (int i = 0; i < items.length; i++) {
            values[i] = serialiser.visit(items[i], 0);
        }

        resolver.bulkInsert(
                Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlFoodItemTable.NAME),
                values);
    }

    private void refreshLocations() {
        Location[] locations = serverManager.getLocations();
        writeLocations(locations);
    }

    private void writeLocations(Location[] u) {
        ContentValues[] values = new ContentValues[u.length];
        for (int i = 0; i < u.length; i++) {
            values[i] = serialiser.visit(u[i], null);
        }

        resolver.bulkInsert(
                Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlLocationTable.NAME),
                values);
    }

    private void refreshUsers() {
        User[] u = serverManager.getUsers();
        writeUsers(u);
    }

    private void writeUsers(User[] u) {
        ContentValues[] values = new ContentValues[u.length];
        for (int i = 0; i < u.length; i++) {
            values[i] = serialiser.visit(u[i], null);
        }

        resolver.bulkInsert(
                Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlUserTable.NAME),
                values);
    }

    private void refreshDevices() {
        UserDevice[] u = serverManager.getDevices();
        writeDevices(u);
    }

    private void writeDevices(UserDevice[] devices) {
        ContentValues[] values = new ContentValues[devices.length];
        for (int i = 0; i < devices.length; i++) {
            values[i] = serialiser.visit(devices[i], null);
        }

        resolver.bulkInsert(
                Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlDeviceTable.NAME),
                values);
    }

    private Update[] readLocalUpdates() {
        Cursor cursor = resolver.query(
                Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlUpdateTable.NAME),
                null,
                null,
                null,
                null,
                null
        );
        assert cursor != null;

        ArrayList<Update> result = new ArrayList<>();
        for (cursor.moveToFirst();
             !cursor.isAfterLast();
             cursor.moveToNext()) {

            String rawDate = cursor.getString(cursor.getColumnIndex(SqlUpdateTable.COL_DATE));
            String tableName = cursor.getString(cursor.getColumnIndex(SqlUpdateTable.COL_NAME));
            try {
                Date date = Config.DATABASE_DATE_FORMAT.parse(rawDate);
                Update u = new Update(tableName, date);
                result.add(u);
            } catch (ParseException e) {
                Log.e(Config.LOG_TAG, "Could not parse date", e);
            }
        }
        cursor.close();
        return result.toArray(new Update[result.size()]);

    }

    private void writeUpdates(Update[] serverUpdates) {
        ContentValues[] values = new ContentValues[serverUpdates.length];

        int index = 0;
        for (Update update : serverUpdates) {
            values[index] = serialiser.visit(update, index);
            index++;
        }

        resolver.bulkInsert(
                Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlUpdateTable.NAME),
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

