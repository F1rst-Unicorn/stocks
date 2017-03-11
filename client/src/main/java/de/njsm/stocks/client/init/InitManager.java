package de.njsm.stocks.client.init;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.config.PropertiesFileHandler;
import de.njsm.stocks.client.exceptions.CryptoException;
import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.frontend.CertificateGenerator;
import de.njsm.stocks.client.frontend.ConfigGenerator;
import de.njsm.stocks.client.frontend.UIFactory;
import de.njsm.stocks.client.network.TcpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

import static de.njsm.stocks.client.config.Configuration.CONFIG_PATH;

public class InitManager {

    private static final Logger LOG = LogManager.getLogger(InitManager.class);

    private final Configuration newConfiguration;
    private final UIFactory f;

    private TcpHost caHost;
    private TcpHost ticketHost;

    private TicketHandler ticketHandler;

    public InitManager(UIFactory f, PropertiesFileHandler fileHandler) {
        this.f = f;
        newConfiguration = new Configuration(fileHandler);
    }

    public void initialise() throws InitialisationException {
        if (isFirstStartup()) {
            LOG.info("Starting initialisation process");
            runFirstInitialisation();
        } else {
            LOG.info("Client is already initialised");
        }
    }

    private boolean isFirstStartup() {
        return ! new File(CONFIG_PATH).exists();
    }

    private void runFirstInitialisation() throws InitialisationException {
        try {
            ticketHandler = new TicketHandler(
                    new KeyStoreHandlerImpl(),
                    new NetworkHandlerImpl());
            initialiseConfigFile();
            getServerProperties(f.getConfigActor());
            createHosts();
            initCertificates(f.getCertGenerator());
            newConfiguration.saveConfig();
        } catch (IOException |
                CryptoException e) {
            LOG.error("Error during initialisation", e);
            LOG.error("Reverting keystore file");
            destroyKeystore();
            throw new InitialisationException("Initialisation failed");
        }
    }

    private void getServerProperties(ConfigGenerator source) {
        String serverName = source.getServerName();
        int[] ports = source.getPorts();

        newConfiguration.setServerName(serverName);
        newConfiguration.setCaPort(ports[0]);
        newConfiguration.setTicketPort(ports[1]);
        newConfiguration.setServerPort(ports[2]);

        LOG.info("Server hostname is " + serverName);
        LOG.info("CA port is " + ports[0]);
        LOG.info("Ticket port is " + ports[1]);
        LOG.info("Server port is " + ports[2]);
    }

    private void createHosts() {
        caHost = new TcpHost(newConfiguration.getServerName(),
                newConfiguration.getCaPort());
        ticketHost = new TcpHost(newConfiguration.getServerName(),
                newConfiguration.getTicketPort());
    }

    private void initCertificates(CertificateGenerator source) throws InitialisationException {
        try {

            String username = source.getUsername();
            String deviceName = source.getDeviceName();
            int uid = source.getUserId();
            int did = source.getDeviceId();
            String fingerprint = source.getCaFingerprint();
            String ticket = source.getTicket();

            ticketHandler.generateKey();
            ticketHandler.verifyServerCa(caHost, fingerprint);
            ticketHandler.generateCsr(username, deviceName, uid, did);
            ticketHandler.handleTicket(ticketHost, ticket, did);

            newConfiguration.setUsername(username);
            newConfiguration.setDeviceName(deviceName);
            newConfiguration.setUserId(uid);
            newConfiguration.setDeviceId(did);
            newConfiguration.setFingerprint(fingerprint);

        } catch (CryptoException e) {
            LOG.error("Error during key creation", e);
            destroyKeystore();
            throw new InitialisationException("Certificate generation failed");
        }
    }

    private void initialiseConfigFile() throws IOException {
        File configFile = new File(CONFIG_PATH);
        boolean newDirectoryWasCreated = configFile.getParentFile().mkdirs();
        if (newDirectoryWasCreated) {
            LOG.info("New path for stocks config created");
        }
    }

    private void destroyKeystore() {
        File keystore = new File(Configuration.KEYSTORE_PATH);
        keystore.delete();
    }

}
