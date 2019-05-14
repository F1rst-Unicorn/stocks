package de.njsm.stocks.android.frontend;

import android.app.Activity;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.util.RefreshViewModel;
import de.njsm.stocks.android.frontend.util.SwipeSyncCallback;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;

public class BaseFragment extends Fragment {

    private static final Logger LOG = new Logger(BaseFragment.class);

    @Override
    public void onStart() {
        super.onStart();
        LOG.d("starting with args " + getArguments());

    }

    protected void maybeShowAddError(StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(requireActivity(), code.getAddErrorMessage());
        }
    }

    public void maybeShowReadError(StatusCode code) {
        maybeShowReadError(requireActivity(), code);
    }

    public void maybeShowEditError(StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(requireActivity(), code.getEditErrorMessage());
        }
    }

    protected void maybeShowDeleteError(StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(requireActivity(), code.getDeleteErrorMessage());
        }
    }

    public static void maybeShowReadError(Activity a, StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(a, code.getReadErrorMessage());
        }
    }

    public static void showErrorMessage(Activity a, int resourceId) {
        new AlertDialog.Builder(a)
                .setTitle(R.string.title_error)
                .setMessage(resourceId)
                .setCancelable(true)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(a.getResources().getString(R.string.dialog_ok), (d, w) -> d.dismiss())
                .create()
                .show();
    }

    protected RefreshViewModel initialiseSwipeRefresh(View view,
                                          int swiperId,
                                          ViewModelProvider.Factory viewModelFactory) {
        SwipeRefreshLayout refresher = view.findViewById(swiperId);
        RefreshViewModel refreshViewModel = ViewModelProviders.of(this, viewModelFactory).get(RefreshViewModel.class);
        refresher.setOnRefreshListener(new SwipeSyncCallback(this, refresher, refreshViewModel));
        return refreshViewModel;
    }
}
