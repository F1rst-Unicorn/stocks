package de.njsm.stocks.backend.network;

import android.content.Context;
import android.content.ContextWrapper;
import de.njsm.stocks.Config;
import de.njsm.stocks.backend.util.AbstractAsyncTask;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.UserDevice;

import java.util.Locale;

public class NewDeviceTask extends AbstractAsyncTask<String, Void, String> {

    private TicketCallback mListener;

    public NewDeviceTask(ContextWrapper c, TicketCallback listener) {
        super(c);
        this.mListener = listener;

    }

    @Override
    protected String doInBackgroundInternally(String... params) {
        int uid = Integer.parseInt(params[1]);
        UserDevice dev = new UserDevice(0, params[0], Integer.parseInt(params[1]));
        Ticket t = ServerManager.m.addDevice(dev);

        return String.format(
                Locale.US,
                "%s\n%s\n%d\n%d\n%s\n%s\n",
                params[2],
                params[0],
                uid,
                t.deviceId,
                context.getSharedPreferences(Config.PREFERENCES_FILE, Context.MODE_PRIVATE).getString(Config.FPR_CONFIG, ""),
                t.ticket);
    }

    @Override
    protected void onPostExecute(String ticket) {
        SyncTask task = new SyncTask(context);
        task.execute();
        mListener.applyToTicket(ticket);
    }

    public interface TicketCallback {

        void applyToTicket(String ticket);
    }
}

