package de.njsm.stocks.android.frontend.setup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.android.util.Logger;

public class SetupBroadcastReceiver extends BroadcastReceiver {

    private static final Logger LOG = new Logger(SetupBroadcastReceiver.class);

    private QrFragment fragment;

    public SetupBroadcastReceiver(QrFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.d("Received action " + intent.getAction());
        fragment.getQrResult(intent.getStringExtra(MainActivity.PARAM_QR_CONTENT));
    }
}
