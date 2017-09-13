package de.njsm.stocks.backend.network.tasks;

import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.backend.util.AbstractAsyncTask;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.UserDevice;

import java.io.File;

public class NewDeviceTask extends AbstractAsyncTask<Object, Void, Ticket> {

    private NetworkManager networkManager;

    private TicketCallback mListener;

    public NewDeviceTask(File exceptionFileDirectory, NetworkManager networkManager, TicketCallback mListener) {
        super(exceptionFileDirectory);
        this.networkManager = networkManager;
        this.mListener = mListener;
    }

    @Override
    protected Ticket doInBackgroundInternally(Object... params) {
        String deviceName = (String) params[0];
        int userId = (int) params[1];
        UserDevice dev = new UserDevice(0, deviceName, userId);
        return ServerManager.m.addDevice(dev);
    }

    @Override
    protected void onPostExecute(Ticket ticket) {
        networkManager.synchroniseData();
        mListener.applyToTicket(ticket);
    }

    public interface TicketCallback {

        void applyToTicket(Ticket ticket);
    }
}

