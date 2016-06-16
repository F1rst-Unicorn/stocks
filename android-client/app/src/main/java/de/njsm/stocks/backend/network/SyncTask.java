package de.njsm.stocks.backend.network;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import de.njsm.stocks.Config;
import de.njsm.stocks.backend.data.Food;
import de.njsm.stocks.backend.data.FoodItem;
import de.njsm.stocks.backend.data.Location;
import de.njsm.stocks.backend.data.Update;
import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.data.UserDevice;
import de.njsm.stocks.backend.db.DatabaseHandler;
import de.njsm.stocks.backend.db.data.SqlDeviceTable;
import de.njsm.stocks.backend.db.data.SqlFoodItemTable;
import de.njsm.stocks.backend.db.data.SqlFoodTable;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.db.data.SqlUserTable;

public class SyncTask extends AsyncTask<Void, Void, Integer> {


    @Override
    protected Integer doInBackground(Void... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        Update[] serverUpdates = ServerManager.m.getUpdates();
        Update[] localUpdates = DatabaseHandler.h.getUpdates();

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

        DatabaseHandler.h.writeUpdates(serverUpdates);
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
        Food[] f = ServerManager.m.getFood();
        DatabaseHandler.h.writeFood(f);
    }

    protected void refreshItems() {
        FoodItem[] f = ServerManager.m.getFoodItems();
        DatabaseHandler.h.writeItems(f);
    }

    protected void refreshLocations() {
        Location[] l = ServerManager.m.getLocations();
        DatabaseHandler.h.writeLocations(l);
    }

    protected void refreshUsers() {
        User[] u = ServerManager.m.getUsers();
        DatabaseHandler.h.writeUsers(u);
    }

    protected void refreshDevices() {
        UserDevice[] d = ServerManager.m.getDevices();
        DatabaseHandler.h.writeDevices(d);
    }
}

