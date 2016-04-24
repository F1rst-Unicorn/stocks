package de.njsm.stocks.authoriser.endpoints;

import de.njsm.stocks.authoriser.db.DatabaseHandler;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/uac")
public class UserGenerator {

    protected static Object lock = new Object();
    DatabaseHandler handler = new DatabaseHandler();
    Logger log = Logger.getLogger("stocks");

    /**
     * Get a new user certificate
     * @return A response containing the new user certificate
     */
    @POST
    @Path("/{ticket}")
    @Consumes("application/octet-stream")
    @Produces("application/octet-stream")
    public Response getNewUser(@PathParam("ticket") String ticket,
                               File csrFile){

        synchronized (lock) {
            try {

                if (!handler.isTicketValid(ticket)) {
                    log.log(Level.SEVERE, "sentry: Got invalid ticket " + ticket);
                    return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
                }

                // save signing request
                int id = handler.getCounter(ticket);
                String userFileName = String.format("user_%d", id);
                String csrFileName = "../CA/intermediate/csr/" + userFileName + ".csr.pem";
                FileOutputStream output = new FileOutputStream(csrFileName);
                FileInputStream input = new FileInputStream(csrFile);
                IOUtils.copy(input, output);
                input.close();
                output.close();

                // check username and device name
                String[] credentials = getCredentials(csrFileName);
                if (!handler.isNameValid(credentials)) {
                    log.log(Level.SEVERE, "sentry: Got invalid username " + credentials[0] + ", " +
                            credentials[2]);
                    return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
                }

                handler.removeTicket(id);
                handler.addUser(credentials);

                // sign request
                log.log(Level.INFO, "sentry: Signing request for " + credentials[0] + credentials[2]);
                String certFile = generateCertificate(userFileName);
                File file = new File(certFile);
                return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                        .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                        .build();

            } catch (SQLException e) {
                log.log(Level.SEVERE, "sentry: Failed to handle request: " + e.getSQLState());
                return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
            } catch (IOException e) {
                log.log(Level.SEVERE, "sentry: Failed to handle request: " + e.getMessage());
                return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
            }
        }
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
    protected String[] getCredentials(String csrFile) throws IOException {
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
            return parseSubjectName(buffer.substring(3, buffer.length()));
        } else {
            throw new IOException("Subject name invalid");
        }
    }

    /**
     * Execute openssl command to generate new certificate
     *
     * @param userFile The userFile string, i.e. the file name without extension
     * @return The relative path of the certificate
     * @throws IOException
     */
    protected String generateCertificate(String userFile) throws IOException {

        String command = String.format("openssl ca " +
                "-config ../CA/intermediate/openssl.cnf " +
                "-extensions usr_cert " +
                "-notext " +
                "-batch " +
                "-md sha256 " +
                "-in ../CA/intermediate/csr/%s.csr.pem " +
                "-out ../CA/intermediate/cert/%s.cert.pem",
                userFile,
                userFile);

        Runtime.getRuntime().exec(command);
        return String.format("../CA/intermediate/cert/%s.cert.pem", userFile);
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
            indices[i] = subject.indexOf('$', last_index);
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
