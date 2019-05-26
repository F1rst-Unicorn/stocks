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
import de.njsm.stocks.client.exceptions.CryptoException;
import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.network.HttpClientFactory;
import de.njsm.stocks.client.network.TcpHost;
import de.njsm.stocks.client.network.sentry.SentryManager;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

class TicketHandler {

    private static final Logger LOG = LogManager.getLogger(TicketHandler.class);

    private final NetworkHandler networkHandler;
    private final KeystoreHandler keystoreHandler;
    private String csr;

    TicketHandler (KeystoreHandler keystoreHandler,
                   NetworkHandler networkHandler) {
        this.keystoreHandler = keystoreHandler;
        this.networkHandler = networkHandler;
    }

    void startBackgroundWork() {
        keystoreHandler.startKeyGeneration();
    }

    void generateKey() throws CryptoException {
        keystoreHandler.generateNewKey();
    }

    void generateCsr(String username, String deviceName, int uid, int did) throws CryptoException {
        String subjectName = generateSubjectName(username, deviceName, uid, did);
        csr = keystoreHandler.generateCsr(subjectName);
    }

    void verifyServerCa(TcpHost caHost, String fingerprint) throws InitialisationException {
        try {
            String caCert = networkHandler.downloadDocument(caHost, "ca");
            String chainCert = networkHandler.downloadDocument(caHost, "chain");

            String fprFromCa = keystoreHandler.getFingerPrintFromPem(caCert);
            if (!fprFromCa.equals(fingerprint)) {
                LOG.error("Fingerprints do not match!");
                LOG.error("Local: " + fingerprint);
                LOG.error("Other: " + fprFromCa);
                throw new InitialisationException("Fingerprints do not match. Search for typing errors or there is a MitM attack");
            }

            keystoreHandler.importCaCertificate(caCert);
            keystoreHandler.importIntermediateCertificate(chainCert);
        } catch (CryptoException e) {
            LOG.error(e);
            throw new InitialisationException("Fingerprint comparison failed", e);
        }
    }

    void handleTicket(TcpHost ticketHost, String ticket, int id) throws InitialisationException {
        try {
            ClientTicket request = new ClientTicket(id, ticket, csr);
            SentryManager sentryManager = createSentryManager(ticketHost);
            networkHandler.setNetworkBackend(sentryManager);
            String certificate = networkHandler.handleTicket(request);
            keystoreHandler.importClientCertificate(certificate);
            keystoreHandler.store();
        } catch (NetworkException e) {
            LOG.error("Sentry communication failed", e);
            throw new InitialisationException("Network communication failed", e);
        } catch (IOException e) {
            LOG.error("Could not store initialised keystore", e);
            throw new InitialisationException("Keystore storing failed", e);
        } catch (CryptoException e) {
            LOG.error(e);
            throw new InitialisationException("Could not handle ticket", e);
        }
    }

    String generateSubjectName(String username, String deviceName, int uid, int did) {
        return String.format("%s$%d$%s$%d", username, uid, deviceName, did);
    }

    private SentryManager createSentryManager(TcpHost ticketHost) throws CryptoException {
        OkHttpClient httpClient = HttpClientFactory.getClient(keystoreHandler.getKeyStore());
        return new SentryManager(httpClient, ticketHost);
    }

}
