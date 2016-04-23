package de.njsm.stocks.internal.auth;

import de.njsm.stocks.internal.Config;

import java.io.IOException;
import java.util.logging.Level;

public class X509CertificateAdmin implements CertificateAdmin {

    @Override
    public void revokeCertificate(int id) {

        String command = String.format("openssl ca " +
                "-config ../CA/intermediate/openssl.cnf " +
                "-revoke ../CA/intermediate/certs/user_%d.cert.pem",
                id);
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e){
            (new Config()).getLog().log(Level.SEVERE,
                    "X509CertificateAdmin: Failed to revoke certificate: " +
                            e.getMessage());
        }

    }
}
