package de.njsm.stocks.android.frontend.eannumber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.util.Consumer;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.android.util.Logger;

public class EanNumberBroadcastReceiver extends BroadcastReceiver {

    private static final Logger LOG = new Logger(EanNumberBroadcastReceiver.class);

    private Consumer<String> eanNumberCreator;

    public EanNumberBroadcastReceiver(Consumer<String> eanNumberCreator) {
        this.eanNumberCreator = eanNumberCreator;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.d("Received action " + intent.getAction());
        eanNumberCreator.accept(intent.getStringExtra(MainActivity.PARAM_QR_CONTENT));
    }
}
