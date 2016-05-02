package de.njsm.stocks.linux.client.frontend;


public interface CertificateGenerator {

    String getTicket();
    String getCaFingerprint();
    String getUsername();
    String getDevicename();
    int[] getUserIds();
}
