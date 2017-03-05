package de.njsm.stocks.client.frontend;


public interface CertificateGenerator {

    String getTicket();
    String getCaFingerprint();
    String getUsername();
    String getDeviceName();
    int getUserId();
    int getDeviceId();
}
