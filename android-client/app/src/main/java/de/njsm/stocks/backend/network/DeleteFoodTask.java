package de.njsm.stocks.backend.network;

import android.content.ContextWrapper;
import de.njsm.stocks.backend.data.Food;
import de.njsm.stocks.backend.util.AbstractAsyncTask;

public class DeleteFoodTask extends AbstractAsyncTask<Food, Void, Integer> {

    public DeleteFoodTask(ContextWrapper c) {
        super(c);
    }

    @Override
    protected Integer doInBackgroundInternally(Food... params) {
        ServerManager.m.removeFood(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        SyncTask task = new SyncTask(context);
        task.execute();
    }
}

