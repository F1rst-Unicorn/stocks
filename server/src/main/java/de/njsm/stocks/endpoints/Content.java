package de.njsm.stocks.endpoints;

import com.sun.net.httpserver.HttpServer;

import de.njsm.stocks.data.Location;
import de.njsm.stocks.internal.Config;
import de.njsm.stocks.internal.auth.UserContext;
import de.njsm.stocks.internal.db.DatabaseHandler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/content")
public class Content {

    private Config c = new Config();

    @GET
    @Path("/location")
    @Produces("application/json")
    public Location[] getLocations() {
        DatabaseHandler handler = c.getDbHandler();

        try {
            return handler.getLocations();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
