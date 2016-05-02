package de.njsm.stocks.linux.client.frontend;


public interface ConfigGenerator {

    void startUp();

    String getServerName();
    int[] getPorts();

    void shutDown();
}
