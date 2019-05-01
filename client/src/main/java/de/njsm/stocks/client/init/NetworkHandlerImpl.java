package de.njsm.stocks.client.init;

import de.njsm.stocks.client.business.data.ClientTicket;
import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.network.TcpHost;
import de.njsm.stocks.client.network.sentry.SentryManager;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkHandlerImpl implements NetworkHandler {

    private static final Logger LOG = LogManager.getLogger(TicketHandler.class);

    private SentryManager networkBackend;

    @Override
    public String downloadDocument(TcpHost host, String resource) throws InitialisationException {
        String url = String.format("http://%s/%s", host.toString(), resource);
        try {
            LOG.info("Downloading certificate from " + url);
            URL website = new URL(url);
            return IOUtils.toString(website.openStream());
        } catch (MalformedURLException e) {
            LOG.error(url + " is not a valid URL", e);
            throw new InitialisationException(url + " is invalid");
        } catch (IOException e) {
            LOG.error("I/O Error", e);
            throw new InitialisationException(e.getMessage());
        }
    }

    @Override
    public void setNetworkBackend(SentryManager backend) {
        networkBackend = backend;
    }

    @Override
    public String handleTicket(ClientTicket ticket) throws NetworkException {
        if (networkBackend == null) {
            throw new NetworkException("No network backend given");
        }
        return networkBackend.requestCertificate(ticket);
    }
}
