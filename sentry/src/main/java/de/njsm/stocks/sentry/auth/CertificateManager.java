package de.njsm.stocks.sentry.auth;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CertificateManager {

    /**
     * Execute openssl command to generate new certificate
     *
     * @param userFile The userFile string, i.e. the file name without extension
     * @return The relative path of the certificate
     * @throws IOException
     */
    public String generateCertificate(String userFile) throws IOException {

        String command = String.format("openssl ca " +
                        "-config ../CA/intermediate/openssl.cnf " +
                        "-extensions usr_cert " +
                        "-notext " +
                        "-batch " +
                        "-md sha256 " +
                        "-in ../CA/intermediate/csr/%s.csr.pem " +
                        "-out ../CA/intermediate/certs/%s.cert.pem",
                userFile,
                userFile);

        Runtime.getRuntime().exec(command);
        return String.format("../CA/intermediate/certs/%s.cert.pem", userFile);
    }

    /**
     * Read the CSR and extract the parts of the Subject name
     *
     * @param csrFile the relative filepath of the CSR to read
     * @return The array of names:
     *          [0]: User name
     *          [1]: User Id
     *          [2]: Device name
     *          [3]: Device Id
     * @throws IOException
     */
    public String[] getPrincipals(String csrFile) throws IOException {
        String command = String.format("openssl req " +
                        "-noout " +
                        "-text " +
                        "-in %s",
                csrFile);

        Process p = Runtime.getRuntime().exec(command);

        String opensslOutput = IOUtils.toString(p.getInputStream());
        p.getInputStream().close();

        Pattern pattern = Pattern.compile("CN=.*\n");
        Matcher match = pattern.matcher(opensslOutput);
        if (match.find()){
            String buffer = match.group(0);
            return parseSubjectName(buffer.substring(3, buffer.length()-1));
        } else {
            throw new IOException("Subject name invalid");
        }
    }

    /**
     * Cut the subject name into the parts which
     * are divided by the $ signs
     *
     * @param subject A subject name
     * @return An array of the divided parts
     * @throws IOException If the subject name has an invalid format
     */
    protected String[] parseSubjectName(String subject) throws IOException{
        int[] indices = new int[3];
        int last_index = 0;

        // find indices of the $ signs
        for (int i = 0; i < 3; i++){
            indices[i] = subject.indexOf('$', last_index + 1);
            last_index = indices[i];
            if (last_index == -1){
                throw new IOException("client name is malformed");
            }
        }

        String username = subject.substring(0, indices[0]);
        String userId = subject.substring(indices[0] + 1, indices[1]);
        String deviceName = subject.substring(indices[1] + 1, indices[2]);
        String deviceId = subject.substring(indices[2] + 1, subject.length());

        return new String[] {username, userId, deviceName, deviceId};

    }
}
