package de.njsm.stocks.android.frontend.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.njsm.stocks.android.service.SetupHandler;
import de.njsm.stocks.android.util.Logger;

public class StartupBroadcastReceiver extends BroadcastReceiver {

    private static final Logger LOG = new Logger(StartupBroadcastReceiver.class);

    private StartupFragment startupFragment;

    public StartupBroadcastReceiver(StartupFragment startupFragment) {
        this.startupFragment = startupFragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.d("Received action " + intent.getAction());
        if (intent.getAction() == null) return;

        switch (intent.getAction()) {

            case SetupHandler.ACTION_UPDATE:
                startupFragment.setDialogMessage(intent.getIntExtra(SetupHandler.PARAM_MESSAGE, 0));
                break;

            case SetupHandler.ACTION_DONE:
                startupFragment.showTerminationDialog(
                        intent.getIntExtra(SetupHandler.PARAM_TITLE, 0),
                        intent.getIntExtra(SetupHandler.PARAM_MESSAGE, 0),
                        intent.getBooleanExtra(SetupHandler.PARAM_SUCCESS, false)
                );
                break;
        }
    }
}
