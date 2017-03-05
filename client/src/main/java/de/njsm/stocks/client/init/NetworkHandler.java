package de.njsm.stocks.client.init;

import de.njsm.stocks.client.data.Ticket;
import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.network.TcpHost;
import de.njsm.stocks.client.network.sentry.SentryManager;

public interface NetworkHandler {

    String downloadDocument(TcpHost host, String resource) throws InitialisationException;

    void setNetworkBackend(SentryManager backend);

    String handleTicket(Ticket ticket) throws NetworkException;

}