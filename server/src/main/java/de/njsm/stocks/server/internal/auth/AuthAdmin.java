package de.njsm.stocks.server.internal.auth;

import de.njsm.stocks.common.data.Principals;

import java.io.IOException;

public interface AuthAdmin {

    String CSR_FORMAT_STRING = "/usr/share/stocks-server/root/CA/intermediate/csr/%s.csr.pem";

    String CERT_FORMAT_STRING = "/usr/share/stocks-server/root/CA/intermediate/certs/%s.cert.pem";

    void saveCsr(int deviceId, String content) throws IOException;

    String getCertificate(int deviceId) throws IOException;

    void wipeDeviceCredentials(int deviceId);

    void generateCertificate(int deviceId) throws IOException;

    Principals getPrincipals(int deviceId) throws IOException;

    void revokeCertificate(int id);

}