package de.njsm.stocks.server.internal.auth;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import de.njsm.stocks.common.data.Principals;
import de.njsm.stocks.common.util.MakerWithExceptions;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import de.njsm.stocks.server.internal.util.HystrixFunction;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.*;

public class X509AuthAdmin implements AuthAdmin {

    private static final Logger LOG = LogManager.getLogger(X509AuthAdmin.class);

    private String csrFormatString;

    private String certFormatString;

    private String caRootDirectory;

    private String reloadCommand;

    private String resourceIdentifier;

    public X509AuthAdmin(String caRootDirectory,
                         String reloadCommand,
                         String resourceIdentifier) {
        this.caRootDirectory = caRootDirectory;
        this.csrFormatString = caRootDirectory + "/intermediate/csr/%s.csr.pem";
        this.certFormatString = caRootDirectory + "/intermediate/certs/%s.cert.pem";
        this.reloadCommand = reloadCommand;
        this.resourceIdentifier = resourceIdentifier;
    }

    @Override
    public synchronized void saveCsr(int deviceId, String content) {
        runSafeAction(() -> {
            FileOutputStream csrFile = new FileOutputStream(getCsrFileName(deviceId));
            IOUtils.write(content, csrFile);
            csrFile.close();
        });
    }

    @Override
    public synchronized void wipeDeviceCredentials(int deviceId) {
        (new File(getCsrFileName(deviceId))).delete();
        (new File(getCertificateFileName(deviceId))).delete();
    }

    public synchronized void generateCertificate(int deviceId) {

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

        runSafeAction(() -> {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
        });
    }

    @Override
    public synchronized String getCertificate(int deviceId) {
        return runSafeAction(() -> {
            FileInputStream input = new FileInputStream(getCertificateFileName(deviceId));
            String result = IOUtils.toString(input);
            input.close();
            return result;
        });
    }

    /**
     * Read the CSR and extract the parts of the Subject name
     *
     * @return The parsed principals
     */
    public synchronized Principals getPrincipals(int deviceId) {
        return runSafeAction(() -> {
            PEMParser parser = new PEMParser(new FileReader(getCsrFileName(deviceId)));
            Object csrRaw = parser.readObject();
            if (csrRaw instanceof PKCS10CertificationRequest) {
                PKCS10CertificationRequest csr = (PKCS10CertificationRequest) csrRaw;
                return HttpsUserContextFactory.parseSubjectName(csr.getSubject().toString());
            } else {
                throw new HystrixBadRequestException("bad request", new SecurityException("failed to cast CSR"));
            }
        });
    }


    public synchronized void revokeCertificate(int id) {
        runSafeAction(() -> {
            String command = String.format("openssl ca " +
                            "-config %s/intermediate/openssl.cnf " +
                            "-batch " +
                            "-revoke %s",
                    caRootDirectory,
                    getCertificateFileName(id));
            Runtime.getRuntime().exec(command).waitFor();
            refreshCrl();
        });
    }

    private void refreshCrl() {
        runSafeAction(() -> {
            String crlCommand = String.format("openssl ca " +
                            "-config %s/intermediate/openssl.cnf " +
                            "-gencrl " +
                            "-out %s/intermediate/crl/intermediate.crl.pem",
                    caRootDirectory,
                    caRootDirectory);

            Runtime.getRuntime().exec(crlCommand).waitFor();

            FileOutputStream out = new FileOutputStream(caRootDirectory + "/intermediate/crl/whole.crl.pem");
            IOUtils.copy(new FileInputStream(caRootDirectory + "/crl/ca.crl.pem"), out);
            IOUtils.copy(new FileInputStream(caRootDirectory + "/intermediate/crl/intermediate.crl.pem"), out);
            out.close();

            Runtime.getRuntime().exec(reloadCommand).waitFor();
        });
    }

    private <R> R runSafeAction(ProducerWithExceptions<R, Exception> client) {
        HystrixFunction<R, Exception> producer = new HystrixFunction<>(resourceIdentifier,
                client);
        return runHystrixFunction(producer);
    }

    private void runSafeAction(MakerWithExceptions<Exception> client) {
        HystrixFunction<Void, Exception> producer = new HystrixFunction<>(resourceIdentifier,
                () -> {
                    client.accept();
                    return null;
                });
        runHystrixFunction(producer);
    }

    private <R> R runHystrixFunction(HystrixFunction<R, Exception> producer) {
        try {
            return producer.execute();
        } catch (HystrixRuntimeException e) {
            if (e.getCause() instanceof RuntimeException) {
                LOG.error("circuit breaker still open");
            } else {
                LOG.error("circuit breaker error", e);
            }
            throw new SecurityException(e.getCause());
        } catch (HystrixBadRequestException e) {
            throw new SecurityException(e.getCause());
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
