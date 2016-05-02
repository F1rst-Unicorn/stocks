package de.njsm.stocks.linux.client;

import de.njsm.stocks.linux.client.frontend.CertificateGenerator;
import de.njsm.stocks.linux.client.frontend.ConfigGenerator;
import de.njsm.stocks.linux.client.network.TicketHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
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
            KeyStore keystore = generateKey(source.getUsername(),
                    source.getDevicename(),
                    source.getUserIds());

            handler.verifyServerCa(source.getCaFingerprint());
            handler.handleTicket(source.getTicket());

            FileOutputStream outStream = new FileOutputStream(CertificateManager.keystorePath);
            keystore.store(outStream, "".toCharArray());
            outStream.close();
        } catch (Exception e) {
            c.getLog().log(Level.SEVERE, "InitManager: Failed to setup keystore: " + e.getMessage());
            File keystore = new File(CertificateManager.keystorePath);
            keystore.delete();
            System.exit(1);
        }
    }

    protected KeyStore generateKey(String username, String devicename, int[] ids) throws Exception {

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] password = "password".toCharArray();
        keystore.load(null, password);

        Enumeration<String> list = keystore.aliases();
        while (list.hasMoreElements()){
            keystore.deleteEntry(list.nextElement());
        }

        // generate key


        // generate CSR


        return keystore;
    }
}
