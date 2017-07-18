package de.njsm.stocks.backend.network;

import android.content.ContextWrapper;
import de.njsm.stocks.backend.data.FoodItem;
import de.njsm.stocks.backend.util.AbstractAsyncTask;

public class MoveItemTask extends AbstractAsyncTask<Void, Void, Void> {

    private FoodItem item;

    private int locId;

    public MoveItemTask(ContextWrapper c,
                        FoodItem i,
                        int locId) {
        super(c);
        this.item = i;
        this.locId = locId;

    }

    @Override
    protected Void doInBackgroundInternally(Void... params) {
        ServerManager.m.move(item, locId);
        return null;
    }

    @Override
    protected void onPostExecute(Void dummy) {
        SyncTask task = new SyncTask(context);
        task.execute();
    }

}

