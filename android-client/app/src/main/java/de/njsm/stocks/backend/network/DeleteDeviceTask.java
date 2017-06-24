package de.njsm.stocks.backend.network;

import android.content.ContextWrapper;
import de.njsm.stocks.backend.data.UserDevice;
import de.njsm.stocks.backend.util.AbstractAsyncTask;

public class DeleteDeviceTask extends AbstractAsyncTask<UserDevice, Void, Integer> {

    public DeleteDeviceTask(ContextWrapper c) {
        super(c);
    }

    @Override
    protected Integer doInBackgroundInternally(UserDevice... params) {
        ServerManager.m.removeDevice(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        SyncTask task = new SyncTask(context);
        task.execute();
    }
}

