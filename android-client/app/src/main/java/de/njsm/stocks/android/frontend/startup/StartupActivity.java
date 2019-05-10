package de.njsm.stocks.android.frontend.startup;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.android.frontend.setup.SetupActivity;
import de.njsm.stocks.android.service.SetupHandler;
import de.njsm.stocks.android.service.SetupService;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.Logger;

public class StartupActivity extends AppCompatActivity {

    private static final Logger LOG = new Logger(StartupActivity.class);

    public static final String ACTION_INPUT_AVAILABLE = "de.njsm.stocks.backend.setup.SetupService.ACTION_INPUT_AVAILABLE";

    private ProgressDialog dialog;

    private StartupBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(Config.PREFERENCES_FILE, Context.MODE_PRIVATE);

        if (getIntent().hasExtra(SetupActivity.SETUP_FINISHED)) {
            LOG.i("Setup data is available, sending to setup service");
            getIntent().getExtras().remove(SetupActivity.SETUP_FINISHED);
            startProgressDialog();
            setupBroadcastReceiver();
            sendInputToBackgroundService();
        } else if (! prefs.contains(Config.USERNAME_CONFIG)) {
            LOG.i("First start up, redirecting to setup activity");
            startBackgroundService();
            startSetupByUser();
        } else {
            LOG.i("Subsequent startup, proceeding normally");
            goToMainActivity();
        }
    }

    void setDialogMessage(int id) {
        if (dialog != null) {
            dialog.setMessage(getResources().getString(id));
        }
    }

    void showTerminationDialog(int title, int message, boolean isSuccess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false);

        if (isSuccess) {
            builder.setIcon(R.drawable.ic_check_black_24dp)
                    .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, which) -> {
                        stopService(new Intent(this, SetupService.class));
                        goToMainActivity();
                        dialog.dismiss();
                    });
        } else {
            builder.setPositiveButton(getResources().getString(R.string.dialog_retry), (dialog, which) -> {
                dialog.dismiss();
                startSetupByUser();
            }).setNegativeButton(getResources().getString(R.string.dialog_abort),(dialog, which) -> {
                dialog.dismiss();
                stopService(new Intent(this, SetupService.class));
                finish();
            }).setIcon(R.drawable.ic_error_black_24dp);
        }

        AlertDialog messageDialog = builder.create();
        dialog.dismiss();
        messageDialog.show();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void startBackgroundService() {
        Intent i = new Intent(this, SetupService.class);
        startService(i);
    }

    private void sendInputToBackgroundService() {
        LOG.d("publishing setup data");
        Intent i = new Intent();
        i.setAction(ACTION_INPUT_AVAILABLE);

        i.putExtras(getIntent().getExtras());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    private void setupBroadcastReceiver() {
        LOG.d("registering setup brr");
        receiver = new StartupBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SetupHandler.ACTION_DONE);
        filter.addAction(SetupHandler.ACTION_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void startProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle(getResources().getString(R.string.dialog_registering));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void startSetupByUser() {
        Intent i = new Intent(this, SetupActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (getIntent().getExtras() != null) {
            i.putExtras(getIntent().getExtras());
        }
        startActivity(i);
    }

    private void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
