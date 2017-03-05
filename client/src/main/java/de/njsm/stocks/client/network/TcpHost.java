package de.njsm.stocks.client.network;

public class TcpHost {

    private String hostname;

    private int port;

    public TcpHost(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return hostname + ":" + port;
    }

    public static boolean isValidPort(int portToCheck) {
        return portToCheck > 0
                && portToCheck < 65536;
    }
}
