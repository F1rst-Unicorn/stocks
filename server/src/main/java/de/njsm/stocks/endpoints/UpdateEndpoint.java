package de.njsm.stocks.endpoints;

import de.njsm.stocks.data.Update;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.sql.SQLException;
import java.util.logging.Level;

@Path("/update")
public class UpdateEndpoint extends Endpoint {

    @GET
    @Produces("application/json")
    public Update[] getUpdates() {

        c.getLog().log(Level.INFO, "UpdateEndpoint: get Updates");

        try {
            return handler.getUpdates();
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "UpdateEndpoint: Failed to get updates: " + e.getMessage());
        }

        return null;
    }
}
