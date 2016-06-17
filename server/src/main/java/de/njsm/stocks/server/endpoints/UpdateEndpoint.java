package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Data;
import de.njsm.stocks.server.data.Update;
import de.njsm.stocks.server.data.UpdateFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.sql.SQLException;
import java.util.logging.Level;

@Path("/update")
public class UpdateEndpoint extends Endpoint {

    @GET
    @Produces("application/json")
    public Data[] getUpdates() {
        c.getLog().log(Level.INFO, "UpdateEndpoint: get Updates");
        return handler.get(UpdateFactory.f);
    }
}
