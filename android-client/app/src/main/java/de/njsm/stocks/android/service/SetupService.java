package de.njsm.stocks.android.service;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Process;
import android.os.*;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import de.njsm.stocks.android.frontend.startup.StartupActivity;
import de.njsm.stocks.android.util.Logger;

public class SetupService extends Service {

    private static final Logger LOG = new Logger(SetupService.class);

    private SetupHandler serviceHandler;

    private HandlerThread thread;

    private BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        LOG.d("setup service created");
        thread = new HandlerThread("setup-thread",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        Looper looper = thread.getLooper();
        serviceHandler = new SetupHandler(looper, this);

        registerBroadcastReceiver();

        Message msg = serviceHandler.obtainMessage(SetupHandler.GENERATE_KEY);
        serviceHandler.sendMessage(msg);
    }

    private void registerBroadcastReceiver() {
        LOG.d("Registering setup service brr");
        receiver = new SetupBroadcastReceiver(serviceHandler);
        IntentFilter filter = new IntentFilter();
        filter.addAction(StartupActivity.ACTION_INPUT_AVAILABLE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        LOG.d("Setup service terminating");
        thread.quitSafely();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
