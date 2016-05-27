package de.njsm.stocks.linux.client;

import com.squareup.okhttp.OkHttpClient;
import de.njsm.stocks.linux.client.frontend.UIFactory;
import de.njsm.stocks.linux.client.network.server.ServerManager;
import de.njsm.stocks.linux.client.storage.DatabaseManager;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Configuration {

    public static final String serverNameConfig = "de.njsm.stocks.client.serverName";
    public static final String caPortConfig = "de.njsm.stocks.client.caPort";
    public static final String ticketPortConfig = "de.njsm.stocks.client.ticketPort";
    public static final String serverPortConfig = "de.njsm.stocks.client.serverPort";
    public static final String userNameConfig = "de.njsm.stocks.client.username";
    public static final String deviceNameConfig = "de.njsm.stocks.client.deviceName";
    public static final String userIdConfig = "de.njsm.stocks.client.userId";
    public static final String deviceIdConfig = "de.njsm.stocks.client.deviceId";
    public static final String fingerprintConfig = "de.njsm.stocks.client.fingerprint";

    public static final String stocksHome = System.getProperty("user.home") + "/.stocks";
    public static final String configPath = stocksHome + "/config";
    public static final String keystorePath = stocksHome + "/keystore";
    public static final String dbPath = stocksHome + "/stocks.db";
    public static final String keystorePassword = System.getProperty("de.njsm.stocks.client.cert.password",
            "thisisapassword");

    protected String serverName;
    protected int caPort;
    protected int ticketPort;
    protected int serverPort;

    protected String username;
    protected String deviceName;
    protected int userId;
    protected int deviceId;
    protected String fingerprint;

    protected final Logger log;
    protected ServerManager sm;
    protected DatabaseManager dm;

    public Configuration () {

        log = Logger.getLogger("stocks-client");
        log.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        log.addHandler(handler);
    }

    public boolean hasConfig() {
        return (new File(configPath).exists());
    }

    public boolean hasCerts() {
        return new File(Configuration.keystorePath).exists();
    }

    public void loadConfig(UIFactory f) {
        InitManager im = null;
        boolean initialised = false;

        if (! hasConfig()) {
            im = new InitManager(this);
            im.initConfig(f.getConfigActor());
            initialised = true;
        }

        if (! hasCerts()) {
            im = (im == null ? new InitManager(this) : im);
            im.initCertificates(f.getCertGenerator());
        }

        if (! initialised) {
            try {
                BufferedReader source = new BufferedReader(new FileReader(configPath));
                Properties p = new Properties();
                p.load(source);

                serverName = p.getProperty(serverNameConfig);
                caPort = Integer.parseInt(p.getProperty(caPortConfig));
                ticketPort = Integer.parseInt(p.getProperty(ticketPortConfig));
                serverPort = Integer.parseInt(p.getProperty(serverPortConfig));

                username = p.getProperty(userNameConfig);
                userId = Integer.parseInt(p.getProperty(userIdConfig));
                deviceName = p.getProperty(deviceNameConfig);
                deviceId = Integer.parseInt(p.getProperty(deviceIdConfig));
                fingerprint = p.getProperty(fingerprintConfig);

            } catch (FileNotFoundException e) {
                getLog().log(Level.SEVERE, "Configuration: Bug in hasConfig(): " + e.getMessage());
            } catch (IOException e) {
                getLog().log(Level.SEVERE,
                        "Configuration: Failed to load config: " + e.getMessage());
            } catch (NumberFormatException e) {
                getLog().log(Level.SEVERE,
                        "Configuration: Malformed configuration file: " + e.getMessage());
            }
        }
    }

    public void saveConfig() {
        try {
            Properties p = new Properties();
            BufferedWriter config = new BufferedWriter(new FileWriter(configPath));
            p.setProperty(serverNameConfig, serverName);
            p.setProperty(caPortConfig, String.valueOf(caPort));
            p.setProperty(ticketPortConfig, String.valueOf(ticketPort));
            p.setProperty(serverPortConfig, String.valueOf(serverPort));
            p.setProperty(userNameConfig, username);
            p.setProperty(deviceNameConfig, deviceName);
            p.setProperty(userIdConfig, String.valueOf(userId));
            p.setProperty(deviceIdConfig, String.valueOf(deviceId));
            p.setProperty(fingerprintConfig, fingerprint);
            p.store(config, "");
            config.close();
        } catch (IOException e){
            getLog().log(Level.SEVERE, "Configuration: Failed to store config: " + e.getMessage());
        }
    }

    public ServerManager getServerManager() {
        if (!hasConfig()){
            throw new RuntimeException("Not initialised!");
        }

        if (sm == null) {
            sm = new ServerManager(this);
        }
        return sm;
    }

    public DatabaseManager getDatabaseManager() {
        if (!hasConfig()){
            throw new RuntimeException("Not initialised!");
        }

        if (dm == null) {
            dm = new DatabaseManager();
        }
        return dm;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setCaPort(int caPort) {
        this.caPort = caPort;
    }

    public void setTicketPort(int ticketPort) {
        this.ticketPort = ticketPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getServerName() {
        return serverName;
    }

    public int getCaPort() {
        return caPort;
    }

    public int getTicketPort() {
        return ticketPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getUsername() {
        return username;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getUserId() {
        return userId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public Logger getLog() {
        return log;
    }

    public OkHttpClient getClient() throws Exception {

        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystorePath),
                keystorePassword.toCharArray());
        tmf.init(ks);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, keystorePassword.toCharArray());

        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.getKeyManagers(),
                tmf.getTrustManagers(),
                new SecureRandom());

        return new OkHttpClient()
                .setSslSocketFactory(context.getSocketFactory())
                .setHostnameVerifier((s, sslSession) -> true);

    }
}
