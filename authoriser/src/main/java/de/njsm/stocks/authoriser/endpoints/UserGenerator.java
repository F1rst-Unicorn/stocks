package de.njsm.stocks.authoriser.endpoints;

import de.njsm.stocks.authoriser.db.DatabaseHandler;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.sql.SQLException;

@Path("/uac")
public class UserGenerator {

    DatabaseHandler handler = new DatabaseHandler();

    /**
     * Get a new user certificate
     * @param ticket An array holding the three strings.
     *               ticket[0] should be the ticket
     *               ticket[1] should be the desired user name
     *               ticket[2] should be the desired device name
     * @return A response containing the new user certificate
     */
    @POST
    @Consumes("application/json")
    @Produces("application/octet-stream")
    public Response getNewUser(String[] ticket){

        try {
            boolean valid = handler.authoriseTicket(ticket);
            if (! valid) {
                return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
            }

            String certPath = generateCertificate(ticket);
            File file = new File(certPath);
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + file.getAbsolutePath() + "\"" )
                    .build();

        } catch (SQLException e){
            return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
        }
    }

    public String generateCertificate(String[] ticket){
        // TODO implement
        return "";
    }

}
