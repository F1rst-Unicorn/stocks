package de.njsm.stocks.android.frontend;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import de.njsm.stocks.R;
import de.njsm.stocks.android.network.server.StatusCode;

public class BaseFragment extends Fragment {

    public void maybeShowAddError(StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(code.getAddErrorMessage());
        }
    }

    public void maybeShowReadError(StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(code.getReadErrorMessage());
        }
    }

    protected void maybeShowDeleteError(StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(code.getDeleteErrorMessage());
        }
    }

    private void showErrorMessage(int resourceId) {
        FragmentActivity activity = getActivity();
        if (activity == null)
            return;

        new AlertDialog.Builder(activity)
                .setTitle(R.string.title_error)
                .setMessage(resourceId)
                .setCancelable(true)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), (d, w) -> d.dismiss())
                .create()
                .show();
    }
}
