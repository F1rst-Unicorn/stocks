package de.njsm.stocks.server.internal.auth;

import de.njsm.stocks.common.data.Principals;
import de.njsm.stocks.common.util.ConsumerWithExceptions;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.*;

public class X509CertificateAdmin implements AuthAdmin {

    private static final Logger LOG = LogManager.getLogger(X509CertificateAdmin.class);

    private String csrFormatString;

    private String certFormatString;

    private String caRootDirectory;

    private String reloadCommand;

    public X509CertificateAdmin(String caRootDirectory,
                                String reloadCommand) {
        this.caRootDirectory = caRootDirectory;
        this.csrFormatString = caRootDirectory + "/intermediate/csr/%s.csr.pem";
        this.certFormatString = caRootDirectory + "/intermediate/certs/%s.cert.pem";
        this.reloadCommand = reloadCommand;
    }

    @Override
    public synchronized void saveCsr(int deviceId, String content) throws IOException {
        FileOutputStream csrFile = new FileOutputStream(getCsrFileName(deviceId));
        IOUtils.write(content, csrFile);
        csrFile.close();
    }

    @Override
    public synchronized void wipeDeviceCredentials(int deviceId) {
        (new File(getCsrFileName(deviceId))).delete();
        (new File(getCertificateFileName(deviceId))).delete();
    }

    public synchronized void generateCertificate(int deviceId) throws IOException {

        String command = String.format("openssl ca " +
                        "-config %s/intermediate/openssl.cnf " +
                        "-extensions usr_cert " +
                        "-notext " +
                        "-batch " +
                        "-md sha256 " +
                        "-in %s " +
                        "-out %s ",
                caRootDirectory,
                getCsrFileName(deviceId),
                getCertificateFileName(deviceId));

        Process p = Runtime.getRuntime().exec(command);
        runInterruptableAction((Void dummy) -> p.waitFor());
    }

    @Override
    public synchronized String getCertificate(int deviceId) throws IOException {
        FileInputStream input = new FileInputStream(getCertificateFileName(deviceId));
        String result = IOUtils.toString(input);
        input.close();
        return result;
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
        runInterruptableAction((Void dummy) -> {

            String command = String.format("openssl ca " +
                            "-config %s/intermediate/openssl.cnf " +
                            "-batch " +
                            "-revoke %s",
                    caRootDirectory,
                    getCertificateFileName(id));
            try {
                Runtime.getRuntime().exec(command).waitFor();
                refreshCrl();
            } catch (IOException e) {
                LOG.error("Failed to revoke certificate", e);
            }
        });
    }

    private void refreshCrl() {
        runInterruptableAction((Void dummy) -> {
            String crlCommand = String.format("openssl ca " +
                            "-config %s/intermediate/openssl.cnf " +
                            "-gencrl " +
                            "-out %s/intermediate/crl/intermediate.crl.pem",
                    caRootDirectory,
                    caRootDirectory);

            try {
                Runtime.getRuntime().exec(crlCommand).waitFor();

                FileOutputStream out = new FileOutputStream(caRootDirectory + "/intermediate/crl/whole.crl.pem");
                IOUtils.copy(new FileInputStream(caRootDirectory + "/crl/ca.crl.pem"), out);
                IOUtils.copy(new FileInputStream(caRootDirectory + "/intermediate/crl/intermediate.crl.pem"), out);
                out.close();

                Runtime.getRuntime().exec(reloadCommand).waitFor();
            } catch (IOException e) {
                LOG.error("Failed to reload CRL", e);
            }
        });
    }

    private void runInterruptableAction(ConsumerWithExceptions<Void, InterruptedException> client) {
        try {
            client.accept(null);
        } catch (InterruptedException e) {
            LOG.error("Interrupted while waiting", e);
        }
    }

    private String getCsrFileName(int deviceId) {
        String userFileName = getFileBaseName(deviceId);
        return String.format(csrFormatString, userFileName);
    }

    private String getCertificateFileName(int deviceId) {
        String userFileName = getFileBaseName(deviceId);
        return String.format(certFormatString, userFileName);
    }

    private String getFileBaseName(int deviceId) {
        return String.format("user_%d", deviceId);
    }
}
