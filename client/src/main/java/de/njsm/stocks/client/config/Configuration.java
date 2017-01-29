package de.njsm.stocks.client.config;

import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.frontend.cli.EnhancedInputReader;
import de.njsm.stocks.client.frontend.cli.InputReader;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.storage.DatabaseManager;

import java.io.*;
import java.util.Properties;
import java.util.logging.*;

public class Configuration {

    public static final String STOCKS_HOME = System.getProperty("user.home") + "/.stocks";
    public static final String CONFIG_PATH = STOCKS_HOME + "/config";
    public static final String KEYSTORE_PATH = STOCKS_HOME + "/keystore";
    public static final String DB_PATH = STOCKS_HOME + "/stocks.db";
    public static final String KEYSTORE_PASSWORD = System.getProperty("de.njsm.stocks.client.cert.password",
            "thisisapassword");

    static final String SERVER_NAME_CONFIG = "de.njsm.stocks.client.serverName";
    static final String CA_PORT_CONFIG = "de.njsm.stocks.client.caPort";
    static final String TICKET_PORT_CONFIG = "de.njsm.stocks.client.ticketPort";
    static final String SERVER_PORT_CONFIG = "de.njsm.stocks.client.serverPort";
    static final String USER_NAME_CONFIG = "de.njsm.stocks.client.username";
    static final String DEVICE_NAME_CONFIG = "de.njsm.stocks.client.deviceName";
    static final String USER_ID_CONFIG = "de.njsm.stocks.client.userId";
    static final String DEVICE_ID_CONFIG = "de.njsm.stocks.client.deviceId";
    static final String FINGERPRINT_CONFIG = "de.njsm.stocks.client.fingerprint";

    private String serverName;
    private int caPort;
    private int ticketPort;
    private int serverPort;

    private String username;
    private String deviceName;
    private int userId;
    private int deviceId;
    private String fingerprint;

    private final Logger log;
    private ServerManager serverInterface;
    private DatabaseManager databaseInterface;
    private final InputReader userInputReader;
    private final PropertiesFileHandler fileHandler;

    public Configuration (PropertiesFileHandler fileHandler) {

        log = Logger.getLogger("stocks-client");
        for (Handler h : log.getHandlers()) {
            log.removeHandler(h);
        }
        log.setLevel(Level.ALL);
        log.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        log.addHandler(handler);

        this.fileHandler = fileHandler;
        userInputReader = new EnhancedInputReader(System.in);
    }

    public void loadConfig() throws InitialisationException {
        try {
            Properties propertiesFromFile = fileHandler.readProperties(CONFIG_PATH);
            populateConfiguration(propertiesFromFile);
            checkSanity();
        } catch (IOException e) {
            throw new InitialisationException("Settings could not be read " +
                    "from " + CONFIG_PATH);
        }
    }

    public void saveConfig() throws InitialisationException {
        try {
            Properties p = populateProperties();
            fileHandler.writePropertiesToFile(CONFIG_PATH, p);
        } catch (IOException e){
            getLog().log(Level.SEVERE, "Configuration: Failed to store config: " + e.getMessage());
            throw new InitialisationException("Could not save config file", e);
        }
    }

    public ServerManager getServerManager() {
        if (serverInterface == null) {
            serverInterface = new ServerManager(this);
        }
        return serverInterface;
    }

    public DatabaseManager getDatabaseManager() {
        if (databaseInterface == null) {
            databaseInterface = new DatabaseManager();
        }
        return databaseInterface;
    }

    public InputReader getReader() {
        return userInputReader;
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

    private void populateConfiguration(Properties p) throws InitialisationException {
        serverName = p.getProperty(SERVER_NAME_CONFIG);
        caPort = getIntProperty(p, CA_PORT_CONFIG);
        ticketPort = getIntProperty(p, TICKET_PORT_CONFIG);
        serverPort = getIntProperty(p, SERVER_PORT_CONFIG);
        username = p.getProperty(USER_NAME_CONFIG);
        userId = getIntProperty(p, USER_ID_CONFIG);
        deviceName = p.getProperty(DEVICE_NAME_CONFIG);
        deviceId = getIntProperty(p, DEVICE_ID_CONFIG);
        fingerprint = p.getProperty(FINGERPRINT_CONFIG);
    }

    private Properties populateProperties() {
        Properties result = new Properties();
        result.setProperty(SERVER_NAME_CONFIG, serverName);
        result.setProperty(CA_PORT_CONFIG, String.valueOf(caPort));
        result.setProperty(TICKET_PORT_CONFIG, String.valueOf(ticketPort));
        result.setProperty(SERVER_PORT_CONFIG, String.valueOf(serverPort));
        result.setProperty(USER_NAME_CONFIG, username);
        result.setProperty(DEVICE_NAME_CONFIG, deviceName);
        result.setProperty(USER_ID_CONFIG, String.valueOf(userId));
        result.setProperty(DEVICE_ID_CONFIG, String.valueOf(deviceId));
        result.setProperty(FINGERPRINT_CONFIG, fingerprint);
        return result;
    }

    private int getIntProperty(Properties source, String key) throws InitialisationException{
        String rawValue = null;
        try {
            rawValue = source.getProperty(key);
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException e) {
            throw new InitialisationException("Configuration '" + key + "' is " +
                    "'" + rawValue + "' which is not a number");
        }
    }

    private void checkSanity() throws InitialisationException {
    }

}
