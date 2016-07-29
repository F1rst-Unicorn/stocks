package de.njsm.stocks.backend.network;

import android.content.Context;
import android.os.AsyncTask;

import de.njsm.stocks.backend.data.User;

public class NewUserTask extends AsyncTask<String, Void, Integer> {

    public Context c;

    public NewUserTask(Context c) {

        this.c = c;

    }

    @Override
    protected Integer doInBackground(String... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        User user = new User(0, params[0]);
        ServerManager.m.addUser(user);

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

