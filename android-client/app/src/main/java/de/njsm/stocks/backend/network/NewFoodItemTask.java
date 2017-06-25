package de.njsm.stocks.backend.network;

import android.content.ContextWrapper;
import de.njsm.stocks.backend.util.AbstractAsyncTask;
import de.njsm.stocks.common.data.FoodItem;

public class NewFoodItemTask extends AbstractAsyncTask<FoodItem, Void, Integer> {

    public NewFoodItemTask(ContextWrapper context) {
        super(context);
    }

    @Override
    protected Integer doInBackgroundInternally(FoodItem... params) {
        ServerManager.m.addItem(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        SyncTask task = new SyncTask(context);
        task.execute();
    }
}

