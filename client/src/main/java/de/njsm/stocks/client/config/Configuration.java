package de.njsm.stocks.client.config;

import de.njsm.stocks.client.exceptions.CryptoException;
import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.exceptions.InvalidConfigException;
import de.njsm.stocks.client.network.HttpClientFactory;
import de.njsm.stocks.client.network.TcpHost;
import de.njsm.stocks.client.network.server.ServerClient;
import de.njsm.stocks.client.network.server.ServerManager;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Properties;

public class Configuration {

    private static final Logger LOG = LogManager.getLogger(Configuration.class);


    public static final String STOCKS_HOME = System.getProperty("user.stocks.dir") + "/.stocks";
    public static final String SYSTEM_STOCKS_HOME = System.getProperty("system.stocks.dir");
    public static final String SYSTEM_DB_PATH = SYSTEM_STOCKS_HOME + "/proto.db";
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

    private ServerManager serverInterface;
    private final PropertiesFileHandler fileHandler;

    public Configuration (PropertiesFileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    public void initialise() throws InitialisationException {
        loadConfig();
        setupServerManager();
    }

    public void loadConfig() throws InitialisationException {
        LOG.info("Loading configuration from " + CONFIG_PATH);
        try {
            Properties propertiesFromFile = fileHandler.readProperties(CONFIG_PATH);
            populateConfiguration(propertiesFromFile);
            checkSanity();
        } catch (IOException e) {
            LOG.error("While reading properties from " + CONFIG_PATH, e);
            throw new InitialisationException("Settings could not be read");
        }
    }

    public void saveConfig() throws InitialisationException {
        LOG.info("Saving configuration to file " + CONFIG_PATH);
        try {
            checkSanity();
            Properties p = populateProperties();
            fileHandler.writePropertiesToFile(CONFIG_PATH, p);
        } catch (IOException e){
            LOG.error("While writing properties to " + CONFIG_PATH, e);
            throw new InitialisationException("Settings could not be saved", e);
        }
    }

    public ServerManager getServerManager() {
        return serverInterface;
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

    protected void populateConfiguration(Properties p) throws InitialisationException {
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

    protected Properties populateProperties() {
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

    protected int getIntProperty(Properties source, String key) throws InitialisationException {
        String rawValue = null;
        try {
            rawValue = source.getProperty(key);
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException e) {
            LOG.error("Configuration contains invalid entry " + key + " -> " + rawValue);
            LOG.error("", e);
            throw new InitialisationException("Configuration '" + key + "' is " +
                    "'" + rawValue + "' which is not a number");
        }
    }

    protected void checkSanity() throws InitialisationException {
        LOG.info("Sanity check started");
        validateString("server name", serverName);
        validatePort(caPort);
        validatePort(ticketPort);
        validatePort(serverPort);
        validateString("username", username);
        validateString("device name", deviceName);
        validateString("fingerprint", fingerprint);
        validateInt("user ID", userId);
        validateInt("device ID", deviceId);
        LOG.info("Sanity check successful");
    }

    protected void validatePort(int port) throws InvalidConfigException {
        if (! TcpHost.isValidPort(port)) {
            LOG.error("Invalid port " + port);
            throw new InvalidConfigException("Port " + port + " is invalid");
        }
    }

    protected void validateString(String key, String value) throws InvalidConfigException {
        if (value == null || value.isEmpty()) {
            LOG.error("Invalid string " + key + " -> " + value);
            throw new InvalidConfigException("'" + value + "' is invalid " +
                    "for " + key);
        }
    }

    protected void validateInt(String key, int value) throws InvalidConfigException {
        if (value <= 0) {
            LOG.error("Invalid int " + key + " -> " + value);
            throw new InvalidConfigException("'" + value + "' is invalid " +
                    "for " + key);
        }
    }

    private void setupServerManager() throws InitialisationException {
        try {
            OkHttpClient httpClient = HttpClientFactory.getClient();
            String url = String.format("https://%s:%d/",
                    serverName,
                    serverPort);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(httpClient)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
            serverInterface = new ServerManager(retrofit.create(ServerClient.class));
        } catch (CryptoException e) {
            LOG.error("While creating HTTP client", e);
            throw new InitialisationException("Keystore could not be read");
        }
    }
}
