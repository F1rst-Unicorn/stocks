package de.njsm.stocks.android.frontend;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import de.njsm.stocks.R;
import de.njsm.stocks.android.network.server.StatusCode;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

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
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_error)
                .setMessage(resourceId)
                .setCancelable(true)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), (d, w) -> d.dismiss())
                .create()
                .show();
    }
}
