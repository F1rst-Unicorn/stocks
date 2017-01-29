package de.njsm.stocks.client.init;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.config.PropertiesFileHandler;
import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.frontend.CertificateGenerator;
import de.njsm.stocks.client.frontend.ConfigGenerator;
import de.njsm.stocks.client.frontend.UIFactory;
import de.njsm.stocks.client.network.TcpHost;

import java.io.File;
import java.io.IOException;

import static de.njsm.stocks.client.config.Configuration.CONFIG_PATH;

public class InitManager {

    private final Configuration newConfiguration;
    private final UIFactory f;

    private TcpHost caHost;
    private TcpHost ticketHost;

    public InitManager(UIFactory f, PropertiesFileHandler fileHandler) {
        this.f = f;
        newConfiguration = new Configuration(fileHandler);
    }

    public void initialise() throws InitialisationException {
        if (isFirstStartup()) {
            runFirstInitialisation();
        } else {
            // TODO Log
        }
    }

    private boolean isFirstStartup() {
        return ! new File(CONFIG_PATH).exists();
    }

    private void runFirstInitialisation() throws InitialisationException {
        try {
            initialiseConfigFile();
            getServerProperties(f.getConfigActor());
            createHosts();
            initCertificates(f.getCertGenerator());
            newConfiguration.saveConfig();
        } catch (IOException e) {
            destroyKeystore();
            throw new InitialisationException("blablabla");
            // TODO Log
        }
    }

    private void getServerProperties(ConfigGenerator source) {
        String serverName = source.getServerName();
        int[] ports = source.getPorts();

        newConfiguration.setServerName(serverName);
        newConfiguration.setCaPort(ports[0]);
        newConfiguration.setTicketPort(ports[1]);
        newConfiguration.setServerPort(ports[2]);
    }

    private void createHosts() {
        caHost = new TcpHost(newConfiguration.getServerName(),
                newConfiguration.getCaPort());
        ticketHost = new TcpHost(newConfiguration.getServerName(),
                newConfiguration.getTicketPort());
    }

    private void initCertificates(CertificateGenerator source) throws InitialisationException {
        TicketHandler handler = new TicketHandler(ticketHost);

        try {
            String username = source.getUsername();
            String deviceName = source.getDeviceName();
            int[] ids = source.getUserIds();
            handler.generateKey(username, deviceName, ids);

            String fingerprint = source.getCaFingerprint();
            handler.verifyServerCa(caHost, fingerprint);

            String ticket = source.getTicket();
            handler.waitFor();
            handler.generateCsr();
            handler.waitFor();
            handler.handleTicket(ticket, ids[1]);

            newConfiguration.setUsername(username);
            newConfiguration.setDeviceName(deviceName);
            newConfiguration.setUserId(ids[0]);
            newConfiguration.setDeviceId(ids[1]);
            newConfiguration.setFingerprint(fingerprint);

        } catch (Exception e) {
            destroyKeystore();
            throw new InitialisationException("Certificate generation failed");
            // TODO Log
        } finally {
            cleanUpTemporaryFiles();
        }
    }

    private void initialiseConfigFile() throws IOException {
        File configFile = new File(CONFIG_PATH);
        configFile.getParentFile().mkdirs();
    }

    private void destroyKeystore() {
        File keystore = new File(Configuration.KEYSTORE_PATH);
        keystore.delete();
    }

    private void cleanUpTemporaryFiles() {
        (new File(TicketHandler.CA_FILE_PATH)).delete();
        (new File(TicketHandler.CSR_FILE_PATH)).delete();
        (new File(TicketHandler.CERT_FILE_PATH)).delete();
        (new File(TicketHandler.INTERMEDIATE_FILE_PATH)).delete();
    }

}
