package de.njsm.stocks.server.util;

public interface AuthAdmin {

    void saveCsr(int deviceId, String content);

    String getCertificate(int deviceId);

    void wipeDeviceCredentials(int deviceId);

    void generateCertificate(int deviceId);

    de.njsm.stocks.server.util.Principals getPrincipals(int deviceId);

    void revokeCertificate(int id);

}