package de.njsm.stocks.client.frontend;


public interface ConfigGenerator {

    void startUp();

    String getServerName();
    int[] getPorts();

    void shutDown();
}
