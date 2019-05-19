package de.njsm.stocks.android.frontend.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.util.Consumer;
import de.njsm.stocks.android.util.Logger;

public class ScanBroadcaseReceiver extends BroadcastReceiver {

    private static final Logger LOG = new Logger(ScanBroadcaseReceiver.class);

    private Consumer<String> consumer;

    public ScanBroadcaseReceiver(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.d("Got action " + intent.getAction());
        consumer.accept(intent.getStringExtra(MainActivity.PARAM_QR_CONTENT));
    }
}
