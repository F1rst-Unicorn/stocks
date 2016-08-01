package de.njsm.stocks.backend.network;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;

public class SwipeSyncCallback implements SwipeRefreshLayout.OnRefreshListener,
        AsyncTaskCallback {

    protected SwipeRefreshLayout mSwiper;
    protected Context c;

    public SwipeSyncCallback(SwipeRefreshLayout mSwiper, Context c) {
        this.mSwiper = mSwiper;
        this.c = c;
    }

    @Override
    public void onRefresh() {
        SyncTask task = new SyncTask(c, this);
        task.execute();
    }

    @Override
    public void onAsyncTaskStart() {
        mSwiper.setRefreshing(true);
    }

    @Override
    public void onAsyncTaskComplete() {
        mSwiper.setRefreshing(false);
    }
}
