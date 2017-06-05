package de.njsm.stocks.client.init;

import com.squareup.okhttp.OkHttpClient;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.client.exceptions.CryptoException;
import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.network.HttpClientFactory;
import de.njsm.stocks.client.network.TcpHost;
import de.njsm.stocks.client.network.sentry.SentryManager;
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
            Ticket request = new Ticket(id, ticket, csr);
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
