package de.njsm.stocks.backend.network;

import android.content.ContextWrapper;
import de.njsm.stocks.backend.util.AbstractAsyncTask;
import de.njsm.stocks.common.data.Location;

public class NewLocationTask extends AbstractAsyncTask<Location, Void, Integer> {


    public NewLocationTask(ContextWrapper context) {
        super(context);
    }

    @Override
    protected Integer doInBackgroundInternally(Location... params) {
        ServerManager.m.addLocation(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer dummy) {
        SyncTask task = new SyncTask(context);
        task.execute();
    }

}

