package de.njsm.stocks.backend.network;

import android.content.Context;
import android.os.AsyncTask;

import de.njsm.stocks.backend.data.UserDevice;

public class DeleteDeviceTask extends AsyncTask<UserDevice, Void, Integer> {

    public Context c;

    public DeleteDeviceTask(Context c) {

        this.c = c;

    }

    @Override
    protected Integer doInBackground(UserDevice... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        ServerManager.m.removeDevice(params[0]);

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

