package de.njsm.stocks.backend.network;

import android.content.Context;
import android.os.AsyncTask;

import de.njsm.stocks.backend.data.FoodItem;
import de.njsm.stocks.backend.data.User;

public class DeleteItemTask extends AsyncTask<FoodItem, Void, Integer> {

    public Context c;

    public DeleteItemTask(Context c) {

        this.c = c;

    }

    @Override
    protected Integer doInBackground(FoodItem... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        ServerManager.m.removeItem(params[0]);

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

