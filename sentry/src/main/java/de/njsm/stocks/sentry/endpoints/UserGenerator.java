package de.njsm.stocks.sentry.endpoints;

import de.njsm.stocks.sentry.db.DatabaseHandler;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/octet-stream")
    public Response getNewCertificate(@PathParam("ticket") String ticket,
                                      @PathParam("id") int deviceId,
                                      @FormDataParam("file") InputStream fileInputStream,
                                      @FormDataParam("file") FormDataContentDisposition fileMetaData){

        try {

            // check ticket validity
            if (! handler.isTicketValid(ticket, deviceId)) {
                throw new Exception("sentry: ticket is not valid");
            }

            // save signing request
            String userFileName = String.format("user_%d", deviceId);
            String csrFileName = "../CA/intermediate/csr/" + userFileName + ".csr.pem";
            FileOutputStream output = new FileOutputStream(csrFileName);
            IOUtils.copy(fileInputStream, output);
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
