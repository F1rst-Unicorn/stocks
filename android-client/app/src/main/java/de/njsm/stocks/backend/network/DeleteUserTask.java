package de.njsm.stocks.backend.network;

import android.content.Context;
import android.os.AsyncTask;

import de.njsm.stocks.backend.data.User;

public class DeleteUserTask extends AsyncTask<User, Void, Integer> {

    public Context c;

    public DeleteUserTask(Context c) {

        this.c = c;

    }

    @Override
    protected Integer doInBackground(User... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        ServerManager.m.removeUser(params[0]);

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

