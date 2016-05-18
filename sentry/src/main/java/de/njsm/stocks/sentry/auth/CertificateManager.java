package de.njsm.stocks.sentry.auth;

import de.njsm.stocks.sentry.data.Principals;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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
     * @return The parsed principals
     * @throws IOException
     */
    public Principals getPrincipals(String csrFile) throws IOException {
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
     * @return The parsed principals
     * @throws IOException If the subject name has an invalid format
     */
    protected Principals parseSubjectName(String subject) throws SecurityException{
        List<Integer> indexList = new LinkedList<>();
        int[] indices;
        int last_index = 0;
        int i = 0;

        // find indices of the $ signs
        while (last_index != -1){
            int newIndex = subject.indexOf('$', last_index + 1);
            if (newIndex != -1) {
                indexList.add(newIndex);
            }
            last_index = newIndex;
        }

        if (indexList.size() != 3){
            throw new SecurityException("client name contains " + indexList.size() + " $ signs");
        }

        indices = new int[indexList.size()];
        for (Integer index : indexList) {
            indices[i] = index;
            i++;
        }

        String[] rawInput = new String[4];
        rawInput[0] = subject.substring(0, indices[0]);
        rawInput[1] = subject.substring(indices[0] + 1, indices[1]);
        rawInput[2] = subject.substring(indices[1] + 1, indices[2]);
        rawInput[3] = subject.substring(indices[2] + 1, subject.length());

        return new Principals(rawInput);

    }
}
