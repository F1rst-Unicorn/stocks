package de.njsm.stocks.backend.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import de.njsm.stocks.Config;
import de.njsm.stocks.backend.data.Update;
import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.data.UserDevice;
import de.njsm.stocks.backend.db.DatabaseHandler;
import de.njsm.stocks.backend.db.data.SqlDeviceTable;
import de.njsm.stocks.backend.db.data.SqlUpdateTable;
import de.njsm.stocks.backend.db.data.SqlUserTable;

public class SyncTask extends AsyncTask<Void, Void, Integer> {

    protected Context c;

    public SyncTask(Context c) {
        this.c = c;
    }

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
        return 0;
    }

    protected void refresh(String table) {
        if (table.equals(SqlUserTable.NAME)) {
            refreshUsers();
        } else if (table.equals(SqlDeviceTable.NAME)) {
            refreshDevices();
        }
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

