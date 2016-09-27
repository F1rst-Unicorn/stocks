package de.njsm.stocks.backend.network;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Locale;

import de.njsm.stocks.Config;
import de.njsm.stocks.backend.data.FoodItem;
import de.njsm.stocks.backend.data.Ticket;
import de.njsm.stocks.backend.data.UserDevice;

public class MoveItemTask extends AsyncTask<Void, Void, Void> {

    public Context c;

    FoodItem item;
    int locId;

    public MoveItemTask(Context c,
                        FoodItem i,
                        int locId) {

        this.c = c;
        this.item = i;
        this.locId = locId;

    }

    @Override
    protected Void doInBackground(Void... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        ServerManager.m.move(item, locId);

        return null;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Void dummy) {
        SyncTask task = new SyncTask(c);
        task.execute();
    }

}

