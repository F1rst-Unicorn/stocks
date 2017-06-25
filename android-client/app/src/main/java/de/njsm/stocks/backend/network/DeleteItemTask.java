package de.njsm.stocks.backend.network;

import android.content.ContextWrapper;
import de.njsm.stocks.backend.util.AbstractAsyncTask;
import de.njsm.stocks.common.data.FoodItem;

public class DeleteItemTask extends AbstractAsyncTask<FoodItem, Void, Integer> {

    public DeleteItemTask(ContextWrapper c) {
        super(c);
    }

    @Override
    protected Integer doInBackgroundInternally(FoodItem... params) {
        ServerManager.m.removeItem(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        SyncTask task = new SyncTask(context);
        task.execute();
    }
}

