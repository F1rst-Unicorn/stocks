package de.njsm.stocks.sentry.auth;

import de.njsm.stocks.common.data.Principals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.FileReader;
import java.io.IOException;

public class CertificateManager {

    private static final Logger LOG = LogManager.getLogger(CertificateManager.class);

    public static final String csrFormatString = "/usr/share/stocks-server/root/CA/intermediate/csr/%s.csr.pem";
    public static final String certFormatString = "/usr/share/stocks-server/root/CA/intermediate/certs/%s.cert.pem";

    /**
     * Execute openssl command to generate new certificate
     *
     * @param userFile The userFile string, i.e. the file name without extension
     */
    public void generateCertificate(String userFile) throws IOException {

        String command = String.format("openssl ca " +
                        "-config /usr/share/stocks-server/root/CA/intermediate/openssl.cnf " +
                        "-extensions usr_cert " +
                        "-notext " +
                        "-batch " +
                        "-md sha256 " +
                        "-in " + csrFormatString + " " +
                        "-out " + certFormatString + " ",
                userFile,
                userFile);

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
     * @param csrFile the relative filepath of the CSR to read
     * @return The parsed principals
     * @throws IOException if IO goes wrong
     */
    public Principals getPrincipals(String csrFile) throws IOException {
        PEMParser parser = new PEMParser(new FileReader(csrFile));
        Object csrRaw = parser.readObject();
        if (csrRaw instanceof PKCS10CertificationRequest) {
            PKCS10CertificationRequest csr = (PKCS10CertificationRequest) csrRaw;
            return parseSubjectName(csr.getSubject().toString());
        } else {
            throw new SecurityException("failed to cast CSR");
        }

    }

    /**
     * Cut the subject name into the parts which
     * are divided by the $ signs
     *
     * @param subject A subject name
     * @return The parsed principals
     */
    public Principals parseSubjectName(String subject){
        int[] indices = new int[3];
        int last_index = subject.lastIndexOf("=");
        int start = last_index;

        // find indices of the $ signs
        for (int i = 0; i < 3; i++){
            indices[i] = subject.indexOf('$', last_index+1);
            last_index = indices[i];
            if (last_index == -1){
                throw new SecurityException("client name is malformed");
            }
        }

        return new Principals(subject.substring(start + 1, indices[0]),
                subject.substring(indices[1] + 1, indices[2]),
                subject.substring(indices[0] + 1, indices[1]),
                subject.substring(indices[2] + 1, subject.length()));

    }
}
