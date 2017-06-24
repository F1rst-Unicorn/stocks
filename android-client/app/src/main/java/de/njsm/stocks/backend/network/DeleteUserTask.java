package de.njsm.stocks.backend.network;

import android.content.ContextWrapper;
import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.util.AbstractAsyncTask;

public class DeleteUserTask extends AbstractAsyncTask<User, Void, Integer> {

    public DeleteUserTask(ContextWrapper context) {
        super(context);
    }

    @Override
    protected Integer doInBackgroundInternally(User... params) {
        ServerManager.m.removeUser(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        SyncTask task = new SyncTask(context);
        task.execute();
    }
}

