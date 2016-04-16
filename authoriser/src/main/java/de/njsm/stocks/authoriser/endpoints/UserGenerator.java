package de.njsm.stocks.authoriser.endpoints;

import de.njsm.stocks.authoriser.db.DatabaseHandler;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.sql.SQLException;

@Path("/uac")
public class UserGenerator {

    DatabaseHandler handler = new DatabaseHandler();

    @GET
    @Consumes("application/json")
    @Produces("application/octet-stream")
    public Response getNewUser(String ticket){

        try {
            boolean valid = handler.authoriseTicket(ticket);
            if (! valid) {
                return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
            }

            // TODO generate certificate

            File file = new File("");
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + file.getAbsolutePath() + "\"" ) //optional
                    .build();

        } catch (SQLException e){
            return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
        }
    }

}
