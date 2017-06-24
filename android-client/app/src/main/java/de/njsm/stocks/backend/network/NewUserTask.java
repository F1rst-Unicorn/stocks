package de.njsm.stocks.backend.network;

import android.content.ContextWrapper;
import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.util.AbstractAsyncTask;

public class NewUserTask extends AbstractAsyncTask<String, Void, Integer> {

    public NewUserTask(ContextWrapper context) {
        super(context);
    }

    @Override
    protected Integer doInBackgroundInternally(String... params) {
        User user = new User(0, params[0]);
        ServerManager.m.addUser(user);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        SyncTask task = new SyncTask(context);
        task.execute();
    }
}

