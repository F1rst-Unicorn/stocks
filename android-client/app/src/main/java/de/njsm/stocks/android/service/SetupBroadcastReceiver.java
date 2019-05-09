package de.njsm.stocks.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import de.njsm.stocks.android.util.Logger;

public class SetupBroadcastReceiver extends BroadcastReceiver {

    private static final Logger LOG = new Logger(SetupBroadcastReceiver.class);

    private SetupHandler serviceHandler;

    public SetupBroadcastReceiver(SetupHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.d("Received action " + intent.getAction());
        Message msg = serviceHandler.obtainMessage(SetupHandler.DO_REST_WORK);
        msg.obj = intent.getExtras();
        serviceHandler.sendMessage(msg);
    }
}
