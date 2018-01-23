package de.njsm.stocks.server.internal.auth;

import de.njsm.stocks.common.data.Principals;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.*;

public class X509CertificateAdmin implements AuthAdmin {

    private static final Logger LOG = LogManager.getLogger(X509CertificateAdmin.class);


    @Override
    public synchronized void saveCsr(int deviceId, String content) throws IOException {
        FileOutputStream csrFile = new FileOutputStream(getCsrFileName(deviceId));
        IOUtils.write(content, csrFile);
        csrFile.close();
    }

    @Override
    public synchronized String getCertificate(int deviceId) throws IOException {
        FileInputStream input = new FileInputStream(getCertificateFileName(deviceId));
        String result = IOUtils.toString(input);
        input.close();
        return result;
    }

    @Override
    public void wipeDeviceCredentials(int deviceId) {
        (new File(getCsrFileName(deviceId))).delete();
        (new File(getCertificateFileName(deviceId))).delete();
    }

    public synchronized void generateCertificate(int deviceId) throws IOException {

        String command = String.format("openssl ca " +
                        "-config /usr/share/stocks-server/root/CA/intermediate/openssl.cnf " +
                        "-extensions usr_cert " +
                        "-notext " +
                        "-batch " +
                        "-md sha256 " +
                        "-in %s " +
                        "-out %s ",
                getCsrFileName(deviceId),
                getCertificateFileName(deviceId));

        Process p = Runtime.getRuntime().exec(command);
        try {
            p.waitFor();
        } catch (InterruptedException e){
            LOG.error("Interrupted: ", e);
        }
    }

    /**
     * Read the CSR and extract the parts of the Subject name
     *
     * @return The parsed principals
     * @throws IOException if IO goes wrong
     */
    public synchronized Principals getPrincipals(int deviceId) throws IOException {
        PEMParser parser = new PEMParser(new FileReader(getCsrFileName(deviceId)));
        Object csrRaw = parser.readObject();
        if (csrRaw instanceof PKCS10CertificationRequest) {
            PKCS10CertificationRequest csr = (PKCS10CertificationRequest) csrRaw;
            return HttpsUserContextFactory.parseSubjectName(csr.getSubject().toString());
        } else {
            throw new SecurityException("failed to cast CSR");
        }

    }


    public synchronized void revokeCertificate(int id) {

        String command = String.format("openssl ca " +
                "-config /usr/share/stocks-server/root/CA/intermediate/openssl.cnf " +
                "-batch " +
                "-revoke /usr/share/stocks-server/root/CA/intermediate/certs/user_%d.cert.pem",
                id);
        try {
            Runtime.getRuntime().exec(command).waitFor();
            refreshCrl();
        } catch (IOException e){
            LOG.error("Failed to revoke certificate", e);
        } catch (InterruptedException e) {
            LOG.error("Interrupted while waiting", e);
        }
    }

    private void refreshCrl() {
        String crlCommand = "openssl ca " +
                "-config /usr/share/stocks-server/root/CA/intermediate/openssl.cnf " +
                "-gencrl " +
                "-out /usr/share/stocks-server/root/CA/intermediate/crl/intermediate.crl.pem";
        String nginxCommand = "sudo /usr/lib/stocks-server/nginx-reload";
        
        try {
            Runtime.getRuntime().exec(crlCommand).waitFor();

            FileOutputStream out = new FileOutputStream("/usr/share/stocks-server/root/CA/intermediate/crl/whole.crl.pem");
            IOUtils.copy(new FileInputStream("/usr/share/stocks-server/root/CA/crl/ca.crl.pem"), out);
            IOUtils.copy(new FileInputStream("/usr/share/stocks-server/root/CA/intermediate/crl/intermediate.crl.pem"), out);
            out.close();

            Runtime.getRuntime().exec(nginxCommand).waitFor();
        } catch (IOException e) {
            LOG.error("Failed to reload CRL", e);
        } catch (InterruptedException e) {
            LOG.error("Interrupted while waiting", e);
        }
    }

    private String getCsrFileName(int deviceId) {
        String userFileName = getFileBaseName(deviceId);
        return String.format(AuthAdmin.CSR_FORMAT_STRING, userFileName);
    }

    private String getCertificateFileName(int deviceId) {
        String userFileName = getFileBaseName(deviceId);
        return String.format(AuthAdmin.CERT_FORMAT_STRING, userFileName);
    }

    private String getFileBaseName(int deviceId) {
        return String.format("user_%d", deviceId);
    }
}
