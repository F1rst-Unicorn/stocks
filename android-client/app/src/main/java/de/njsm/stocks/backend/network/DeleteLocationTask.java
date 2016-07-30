package de.njsm.stocks.backend.network;

import android.content.Context;
import android.os.AsyncTask;

import de.njsm.stocks.backend.data.Location;
import de.njsm.stocks.backend.data.User;

public class DeleteLocationTask extends AsyncTask<Location, Void, Integer> {

    public Context c;

    public DeleteLocationTask(Context c) {

        this.c = c;

    }

    @Override
    protected Integer doInBackground(Location... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        ServerManager.m.removeLocation(params[0]);

        return 0;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Integer integer) {
        SyncTask task = new SyncTask(c);
        task.execute();
    }
}

