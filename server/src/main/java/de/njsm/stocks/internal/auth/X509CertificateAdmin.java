package de.njsm.stocks.internal.auth;

import de.njsm.stocks.internal.Config;
import org.apache.commons.io.IOUtils;
import sun.nio.ch.IOUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class X509CertificateAdmin implements CertificateAdmin {

    @Override
    public void revokeCertificate(int id) {

        String command = String.format("openssl ca " +
                "-config ../CA/intermediate/openssl.cnf " +
                "-batch " +
                "-revoke ../CA/intermediate/certs/user_%d.cert.pem",
                id);
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            refreshCrl();
        } catch (IOException e){
            (new Config()).getLog().log(Level.SEVERE,
                    "X509CertificateAdmin: Failed to revoke certificate: " +
                            e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    protected void refreshCrl() {
        String crlCommand = "openssl ca " +
                "-config ../CA/intermediate/openssl.cnf " +
                "-gencrl " +
                "-out ../CA/intermediate/crl/intermediate.crl.pem";
        try {
            FileOutputStream out = new FileOutputStream("../CA/intermediate/crl/whole.crl.pem");
            IOUtils.copy(new FileInputStream("../CA/crl/ca.crl.pem"), out);
            IOUtils.copy(new FileInputStream("../CA/intermediate/crl/intermediate.crl.pem"), out);
            out.close();

            String nginxCommand = "sudo /usr/lib/stocks-server/nginx-reload";

            Process p = Runtime.getRuntime().exec(crlCommand);
            p.waitFor();
            p = Runtime.getRuntime().exec(nginxCommand);
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
