package de.njsm.stocks.backend.network;

import android.content.Context;
import android.os.AsyncTask;

import de.njsm.stocks.backend.data.Location;

public class NewLocationTask extends AsyncTask<Location, Void, Integer> {

    public Context c;

    public NewLocationTask(Context c) {

        this.c = c;

    }

    @Override
    protected Integer doInBackground(Location... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        ServerManager.m.addLocation(params[0]);

        return 0;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Integer dummy) {
        SyncTask task = new SyncTask(c);
        task.execute();
    }

}

