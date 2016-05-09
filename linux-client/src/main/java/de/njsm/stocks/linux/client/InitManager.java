package de.njsm.stocks.linux.client;

import de.njsm.stocks.linux.client.frontend.CertificateGenerator;
import de.njsm.stocks.linux.client.frontend.ConfigGenerator;
import de.njsm.stocks.linux.client.network.TicketHandler;
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
            c.saveConfig();

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

            generateKey(username, deviceName, ids);

            handler.verifyServerCa(source.getCaFingerprint());
            handler.handleTicket(source.getTicket(), ids[1]);

        } catch (Exception e) {
            c.getLog().log(Level.SEVERE, "InitManager: Failed to setup keystore: " + e.getMessage());
            File keystore = new File(CertificateManager.keystorePath);
            keystore.delete();
            System.exit(1);
        }
    }

    protected void generateKey(String username, String devicename, int[] ids) throws Exception {

        // generate key
        String cn = String.format("%s$%d$%s$%d", username, ids[0], devicename, ids[1]);
        String keyGenCommand = String.format("keytool -genkeypair " +
                "-dname CN=%s,OU=%s,O=%s " +
                "-alias %s " +
                "-keyalg RSA " +
                "-keysize 4096 " +
                "-keypass %s " +
                "-keystore %s " +
                "-storepass %s ",
                cn,
                "User",
                "stocks",
                "client",
                CertificateManager.keystorePassword,
                CertificateManager.keystorePath,
                CertificateManager.keystorePassword);
        Process p = Runtime.getRuntime().exec(keyGenCommand);
        InputStream resultStream = p.getInputStream();
        InputStream errorStream = p.getErrorStream();
        IOUtils.copy(resultStream, System.out);
        IOUtils.copy(errorStream, System.out);

        // generate CSR
        String getCsrCommand = String.format("keytool -certreq " +
                "-alias client " +
                "-file %s/client.csr.pem " +
                "-keypass %s " +
                "-keystore %s " +
                "-storepass %s ",
                Configuration.stocksHome,
                CertificateManager.keystorePassword,
                CertificateManager.keystorePath,
                CertificateManager.keystorePassword);
        p = Runtime.getRuntime().exec(getCsrCommand);
        resultStream = p.getInputStream();
        errorStream = p.getErrorStream();
        IOUtils.copy(resultStream, System.out);
        IOUtils.copy(errorStream, System.out);


    }
}
