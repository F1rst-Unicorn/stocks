package de.njsm.stocks.backend.network;

import android.content.ContextWrapper;
import android.support.v4.widget.SwipeRefreshLayout;

public class SwipeSyncCallback implements SwipeRefreshLayout.OnRefreshListener,
        AsyncTaskCallback {

    protected SwipeRefreshLayout mSwiper;
    protected ContextWrapper c;

    public SwipeSyncCallback(SwipeRefreshLayout mSwiper, ContextWrapper c) {
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
