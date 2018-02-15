package de.njsm.stocks.server.internal.auth;

import de.njsm.stocks.common.data.Principals;

public interface AuthAdmin {

    void saveCsr(int deviceId, String content);

    String getCertificate(int deviceId);

    void wipeDeviceCredentials(int deviceId);

    void generateCertificate(int deviceId);

    Principals getPrincipals(int deviceId);

    void revokeCertificate(int id);

}