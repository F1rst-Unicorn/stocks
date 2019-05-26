/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
import java.nio.charset.StandardCharsets;

public class NetworkHandlerImpl implements NetworkHandler {

    private static final Logger LOG = LogManager.getLogger(TicketHandler.class);

    private SentryManager networkBackend;

    @Override
    public String downloadDocument(TcpHost host, String resource) throws InitialisationException {
        String url = String.format("http://%s/%s", host.toString(), resource);
        try {
            LOG.info("Downloading certificate from " + url);
            URL website = new URL(url);
            return IOUtils.toString(website.openStream(), StandardCharsets.UTF_8);
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
