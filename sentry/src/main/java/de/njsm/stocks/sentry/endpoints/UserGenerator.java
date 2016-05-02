package de.njsm.stocks.sentry.endpoints;

import de.njsm.stocks.sentry.db.DatabaseHandler;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/uac")
public class UserGenerator {

    DatabaseHandler handler = new DatabaseHandler();
    Logger log = Logger.getLogger("stocks");

    /**
     * Get a new user certificate
     * @return A response containing the new user certificate
     */
    @POST
    @Path("/{ticket}/{id}")
    @Consumes("application/octet-stream")
    @Produces("application/octet-stream")
    public Response getNewCertificate(@PathParam("ticket") String ticket,
                                      @PathParam("id") int deviceId,
                                      File csrFile){

        try {

            // check ticket validity
            if (! handler.isTicketValid(ticket, deviceId)) {
                throw new Exception("sentry: ticket is not valid");
            }

            // save signing request
            String userFileName = String.format("user_%d", deviceId);
            String csrFileName = "../CA/intermediate/csr/" + userFileName + ".csr.pem";
            FileOutputStream output = new FileOutputStream(csrFileName);
            FileInputStream input = new FileInputStream(csrFile);
            IOUtils.copy(input, output);
            input.close();
            output.close();

            // hand ticket and deviceId to database handler
            handler.handleTicket(ticket, deviceId);

            // Send answer to client
            File file = new File(String.format("../CA/intermediate/cert/" + userFileName + ".cert.pem"));
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                    .build();

        } catch (Exception e) {
            log.log(Level.SEVERE, "sentry: Failed to handle request: " + e.getMessage());
            return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
        }
    }

}
