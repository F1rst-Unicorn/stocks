package de.njsm.stocks.linux.client;

import de.njsm.stocks.linux.client.frontend.UIFactory;
import de.njsm.stocks.linux.client.network.server.ServerManager;

import java.io.*;
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
    public static final String stocksHome = System.getProperty("user.home") + "/.stocks";
    public static final String configPath = stocksHome + "/config";

    protected String serverName;
    protected int caPort;
    protected int ticketPort;
    protected int serverPort;

    protected final Logger log;
    protected ServerManager sm;

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

    public void loadConfig(UIFactory f) {

        if (! hasConfig()) {
            (new InitManager(this)).initConfig(f.getInteractor());
            return;
        }

        try {
            BufferedReader source = new BufferedReader(new FileReader(configPath));
            Properties p = new Properties();
            p.load(source);

            serverName = p.getProperty(serverNameConfig);
            caPort = Integer.parseInt(p.getProperty(caPortConfig));
            ticketPort = Integer.parseInt(p.getProperty(ticketPortConfig));
            serverPort = Integer.parseInt(p.getProperty(serverPortConfig));

        } catch (FileNotFoundException e){
            getLog().log(Level.SEVERE, "Configuration: Bug in hasConfig(): " + e.getMessage());
        } catch (IOException e) {
            getLog().log(Level.SEVERE,
                    "Configuration: Failed to load config: " + e.getMessage());
        } catch (NumberFormatException e) {
            getLog().log(Level.SEVERE,
                    "Configuration: Malformed configuration file: " + e.getMessage());
        }

    }

    public void saveConfig() {
        try {
            Properties p = new Properties();
            BufferedWriter config = new BufferedWriter(new FileWriter(configPath));
            p.setProperty(Configuration.serverNameConfig, serverName);
            p.setProperty(Configuration.caPortConfig, String.valueOf(caPort));
            p.setProperty(Configuration.ticketPortConfig, String.valueOf(ticketPort));
            p.setProperty(Configuration.serverPortConfig, String.valueOf(serverPort));
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

    public Logger getLog() {
        return log;
    }
}
