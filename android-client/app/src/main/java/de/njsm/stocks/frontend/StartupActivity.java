package de.njsm.stocks.frontend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import de.njsm.stocks.backend.setup.SetupTask;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.frontend.setup.SetupActivity;
import de.njsm.stocks.frontend.setup.SetupFinishedListener;

public class StartupActivity extends AppCompatActivity implements SetupFinishedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(Config.PREFERENCES_FILE, Context.MODE_PRIVATE);

        if (getIntent().hasExtra(SetupActivity.SETUP_FINISHED)) {
            Log.i(Config.LOG_TAG, "Setup data is available, starting setup task");
            startBackgroundSetupTask();
        } else if (! prefs.contains(Config.USERNAME_CONFIG)) {
            Log.i(Config.LOG_TAG, "First start up, redirecting to setup activity");
            startSetupByUser();
        } else {
            Log.i(Config.LOG_TAG, "Subsequent startup, proceeding normally");
            goToMainActivity();
        }
    }

    private void startBackgroundSetupTask() {
        getIntent().getExtras().remove(SetupActivity.SETUP_FINISHED);
        SetupTask s = new SetupTask(this);
        s.registerListener(this);
        s.execute();
    }

    private void startSetupByUser() {
        Intent i = new Intent(this, SetupActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onSetupFinished() {
        goToMainActivity();
    }
}
