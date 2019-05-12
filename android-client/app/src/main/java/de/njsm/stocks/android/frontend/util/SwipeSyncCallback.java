package de.njsm.stocks.android.frontend.util;


import androidx.lifecycle.LiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;

public class SwipeSyncCallback implements SwipeRefreshLayout.OnRefreshListener {

    private BaseFragment owner;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RefreshViewModel refreshViewModel;

    public SwipeSyncCallback(BaseFragment owner,
                             SwipeRefreshLayout swipeRefreshLayout,
                             RefreshViewModel refreshViewModel) {
        this.owner = owner;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.refreshViewModel = refreshViewModel;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        LiveData<StatusCode> result = refreshViewModel.refresh();
        result.observe(owner, code -> {
            swipeRefreshLayout.setRefreshing(false);
            result.removeObservers(owner);
            owner.maybeShowReadError(code);
        });
    }
}
