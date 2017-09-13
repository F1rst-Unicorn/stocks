package de.njsm.stocks.backend.network;

import android.support.v4.widget.SwipeRefreshLayout;

public class SwipeSyncCallback implements SwipeRefreshLayout.OnRefreshListener,
        AsyncTaskCallback {

    protected SwipeRefreshLayout mSwiper;

    private NetworkManager networkManager;

    public SwipeSyncCallback(SwipeRefreshLayout mSwiper, NetworkManager networkManager) {
        this.mSwiper = mSwiper;
        this.networkManager = networkManager;
    }

    @Override
    public void onRefresh() {
        networkManager.synchroniseData(this);
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
