package de.njsm.stocks.backend.network;

import android.content.ContextWrapper;
import de.njsm.stocks.backend.data.Food;
import de.njsm.stocks.backend.util.AbstractAsyncTask;

public class NewFoodTask extends AbstractAsyncTask<Food, Void, Integer> {

    public NewFoodTask(ContextWrapper context) {
        super(context);
    }

    @Override
    protected Integer doInBackgroundInternally(Food... params) {
        ServerManager.m.addFood(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        SyncTask task = new SyncTask(context);
        task.execute();
    }
}

