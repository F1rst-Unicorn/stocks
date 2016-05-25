package de.njsm.stocks.linux.client;

import de.njsm.stocks.linux.client.frontend.CertificateGenerator;
import de.njsm.stocks.linux.client.frontend.ConfigGenerator;
import de.njsm.stocks.linux.client.network.sentry.TicketHandler;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class InitManager {

    Configuration c;

    public InitManager (Configuration c) {
        this.c = c;
    }

    public void initConfig(ConfigGenerator source) {

        source.startUp();
        generateConfig(source.getServerName(),
                       source.getPorts());

        source.shutDown();
    }

    protected void generateConfig(String serverName, int[] ports) {

        try {
            File configFile = new File(Configuration.configPath);
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();

            c.setServerName(serverName);
            c.setCaPort(ports[0]);
            c.setTicketPort(ports[1]);
            c.setServerPort(ports[2]);

        } catch (IOException e) {
            c.getLog().log(Level.SEVERE, "InitManager: Failed to write config: " + e.getMessage());
            File configFile = new File(Configuration.configPath);
            configFile.delete();
        }
    }

    public void initCertificates(CertificateGenerator source) {

        TicketHandler handler = new TicketHandler(c);

        try {
            String username = source.getUsername();
            String deviceName = source.getDevicename();
            int[] ids = source.getUserIds();

            handler.generateKey(username, deviceName, ids);
            handler.generateCsr();
            String fingerprint = source.getCaFingerprint();
            handler.verifyServerCa(fingerprint);
            handler.handleTicket(source.getTicket(), ids[1]);

            c.setUsername(username);
            c.setDeviceName(deviceName);
            c.setUserId(ids[0]);
            c.setDeviceId(ids[1]);
            c.setFingerprint(fingerprint);
            c.saveConfig();

        } catch (Exception e) {
            c.getLog().log(Level.SEVERE, "InitManager: Failed to setup keystore: " + e.getMessage());
            File keystore = new File(Configuration.keystorePath);
            keystore.delete();
            System.exit(1);
        } finally {
            (new File(TicketHandler.caFilePath)).delete();
            (new File(TicketHandler.csrFilePath)).delete();
            (new File(TicketHandler.certFilePath)).delete();
            (new File(TicketHandler.intermediateFilePath)).delete();
            (new File(Configuration.stocksHome + "/client.chain.cert.pem")).delete();
        }
    }


}
