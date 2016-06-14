package de.njsm.stocks.backend.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import de.njsm.stocks.Config;
import de.njsm.stocks.backend.data.Update;
import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.db.DatabaseHandler;

public class SyncTask extends AsyncTask<Void, Void, Integer> {

    protected Context c;
    protected DatabaseHandler db;

    public SyncTask(Context c) {
        this.c = c;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        db = new DatabaseHandler(c);
        Update[] serverUpdates = ServerManager.m.getUpdates();
        Update[] localUpdates = db.getUpdates();

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

        db.writeUpdates(serverUpdates);
        return 0;
    }

    protected void refresh(String table) {
        if (table.equals("User")) {
            refreshUsers();
        }
    }

    protected void refreshUsers() {
        User[] u = ServerManager.m.getUsers();
        db.writeUsers(u);
    }
}

