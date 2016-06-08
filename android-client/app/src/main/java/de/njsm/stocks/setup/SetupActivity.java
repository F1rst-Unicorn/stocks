package de.njsm.stocks.setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        if (prefs.contains(Config.serverName)) {
            getExtras().putString(Config.serverName, prefs.getString(Config.serverName, ""));
        }
        if (prefs.contains(Config.caPort)) {
            getExtras().putInt(Config.caPort, prefs.getInt(Config.caPort, 0));
        }
        if (prefs.contains(Config.sentryPort)) {
            getExtras().putInt(Config.sentryPort, prefs.getInt(Config.sentryPort, 0));
        }
        if (prefs.contains(Config.serverPort)) {
            getExtras().putInt(Config.serverPort, prefs.getInt(Config.serverPort, 0));
        }
        if (prefs.contains(Config.username)) {
            getExtras().putString(Config.username, prefs.getString(Config.username, ""));
        }
        if (prefs.contains(Config.deviceName)) {
            getExtras().putString(Config.deviceName, prefs.getString(Config.deviceName, ""));
        }
        if (prefs.contains(Config.uid)) {
            getExtras().putInt(Config.uid, prefs.getInt(Config.uid, 0));
        }
        if (prefs.contains(Config.did)) {
            getExtras().putInt(Config.did, prefs.getInt(Config.did, 0));
        }
        if (prefs.contains(Config.fpr)) {
            getExtras().putString(Config.fpr, prefs.getString(Config.fpr, ""));
        }
        if (prefs.contains(Config.ticket)) {
            getExtras().putString(Config.ticket, prefs.getString(Config.ticket, ""));
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
                .putString(Config.serverName, data.getString(Config.serverName))
                .putInt(Config.caPort, data.getInt(Config.caPort))
                .putInt(Config.sentryPort, data.getInt(Config.sentryPort))
                .putInt(Config.serverPort, data.getInt(Config.serverPort))
                .putString(Config.username, data.getString(Config.username))
                .putString(Config.deviceName, data.getString(Config.deviceName))
                .putInt(Config.uid, data.getInt(Config.uid))
                .putInt(Config.did, data.getInt(Config.did))
                .putString(Config.fpr, data.getString(Config.fpr))
                .putString(Config.ticket, data.getString(Config.ticket))
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
