package de.njsm.stocks.setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.github.fcannizzaro.materialstepper.style.DotStepper;

import de.njsm.stocks.Config;
import de.njsm.stocks.MainActivity;
import de.njsm.stocks.R;
import de.njsm.stocks.zxing.IntentIntegrator;
import de.njsm.stocks.zxing.IntentResult;

public class SetupActivity extends DotStepper {

    protected ServerFragment serverFragment;
    protected QrFragment qrFragment;
    protected PrincipalsFragment principalsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTitle("Connection Setup");

        serverFragment = new ServerFragment();
        qrFragment = new QrFragment();
        principalsFragment = new PrincipalsFragment();

        SharedPreferences prefs = new Config(this).getPrefs();
        if (prefs.contains(Config.serverNameConfig)) {
            getExtras().putString(Config.serverNameConfig, prefs.getString(Config.serverNameConfig, ""));
        }
        if (prefs.contains(Config.caPortConfig)) {
            getExtras().putInt(Config.caPortConfig, prefs.getInt(Config.caPortConfig, 0));
        }
        if (prefs.contains(Config.sentryPortConfig)) {
            getExtras().putInt(Config.sentryPortConfig, prefs.getInt(Config.sentryPortConfig, 0));
        }
        if (prefs.contains(Config.serverPortConfig)) {
            getExtras().putInt(Config.serverPortConfig, prefs.getInt(Config.serverPortConfig, 0));
        }
        if (prefs.contains(Config.usernameConfig)) {
            getExtras().putString(Config.usernameConfig, prefs.getString(Config.usernameConfig, ""));
        }
        if (prefs.contains(Config.deviceNameConfig)) {
            getExtras().putString(Config.deviceNameConfig, prefs.getString(Config.deviceNameConfig, ""));
        }
        if (prefs.contains(Config.uidConfig)) {
            getExtras().putInt(Config.uidConfig, prefs.getInt(Config.uidConfig, 0));
        }
        if (prefs.contains(Config.didConfig)) {
            getExtras().putInt(Config.didConfig, prefs.getInt(Config.didConfig, 0));
        }
        if (prefs.contains(Config.fprConfig)) {
            getExtras().putString(Config.fprConfig, prefs.getString(Config.fprConfig, ""));
        }
        if (prefs.contains(Config.ticketConfig)) {
            getExtras().putString(Config.ticketConfig, prefs.getString(Config.ticketConfig, ""));
        }

        addStep(serverFragment);
        addStep(qrFragment);
        addStep(principalsFragment);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onComplete(Bundle data) {

        SharedPreferences prefs = new Config(this).getPrefs();
        prefs.edit()
                .putString(Config.serverNameConfig, data.getString(Config.serverNameConfig))
                .putInt(Config.caPortConfig, data.getInt(Config.caPortConfig))
                .putInt(Config.sentryPortConfig, data.getInt(Config.sentryPortConfig))
                .putInt(Config.serverPortConfig, data.getInt(Config.serverPortConfig))
                .putString(Config.usernameConfig, data.getString(Config.usernameConfig))
                .putString(Config.deviceNameConfig, data.getString(Config.deviceNameConfig))
                .putInt(Config.uidConfig, data.getInt(Config.uidConfig))
                .putInt(Config.didConfig, data.getInt(Config.didConfig))
                .putString(Config.fprConfig, data.getString(Config.fprConfig))
                .putString(Config.ticketConfig, data.getString(Config.ticketConfig))
                .commit();

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("setup", "setup");
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            qrFragment.getQrResult(scanResult);
            onNext();

        }
        // else continue with any other code you need in the method
    }

    public void onExpertPush(View view) {
        Switch s = (Switch) view;
        LinearLayout l = (LinearLayout) findViewById(R.id.expert_options);
        if (l == null) {
            return;
        }
        if (s.isChecked()) {
            l.setVisibility(View.VISIBLE);
            for (int i = 0; i < l.getChildCount(); i++) {
                l.getChildAt(i).setVisibility(View.VISIBLE);
            }
        } else {
            l.setVisibility(View.GONE);
            for (int i = 0; i < l.getChildCount(); i++) {
                l.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

}
