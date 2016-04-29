package de.njsm.stocks.linux.client.frontend;


public interface ConfigGenerator {

    void startUp();

    String getServerName();
    int[] getPorts();

    String getTicket();
    String getCaFingerprint();
    String getUsername();
    String getDevicename();
    int[] getUserIds();

    void shutDown();
}
