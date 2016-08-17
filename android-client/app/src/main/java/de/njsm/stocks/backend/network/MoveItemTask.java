package de.njsm.stocks.backend.network;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Locale;

import de.njsm.stocks.Config;
import de.njsm.stocks.backend.data.Ticket;
import de.njsm.stocks.backend.data.UserDevice;

public class MoveItemTask extends AsyncTask<String, Void, String> {

    public Context c;
    protected TicketCallback mListener;

    public MoveItemTask(Context c, TicketCallback listener) {

        this.c = c;
        this.mListener = listener;

    }

    @Override
    protected String doInBackground(String... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

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
                c.getSharedPreferences(Config.preferences, Context.MODE_PRIVATE).getString(Config.fprConfig, ""),
                t.ticket);
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(String ticket) {
        SyncTask task = new SyncTask(c);
        task.execute();
        mListener.applyToTicket(ticket);
    }

    public interface TicketCallback {

        void applyToTicket(String ticket);
    }
}

