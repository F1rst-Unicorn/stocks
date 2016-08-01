package de.njsm.stocks.backend.network;

import android.content.Context;
import android.os.AsyncTask;

import de.njsm.stocks.backend.data.Food;
import de.njsm.stocks.backend.data.User;

public class NewFoodTask extends AsyncTask<Food, Void, Integer> {

    public Context c;

    public NewFoodTask(Context c) {

        this.c = c;

    }

    @Override
    protected Integer doInBackground(Food... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        ServerManager.m.addFood(params[0]);

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

