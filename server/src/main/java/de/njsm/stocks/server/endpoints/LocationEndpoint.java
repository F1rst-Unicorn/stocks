package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Data;
import de.njsm.stocks.server.data.Location;
import de.njsm.stocks.server.data.LocationFactory;

import javax.ws.rs.*;
import java.util.logging.Level;


@Path("/location")
public class LocationEndpoint extends Endpoint {

    @GET
    @Produces("application/json")
    public Data[] getLocations() {
        c.getLog().log(Level.INFO, "LocationEndpoint: Get locations");
        return handler.get(LocationFactory.f);
    }

    @PUT
    @Consumes("application/json")
    public void addLocation(Location loc){
        c.getLog().log(Level.INFO, "LocationEndpoint: Add location " + loc.name);
        handler.add(loc);
    }

    @PUT
    @Consumes("application/json")
    @Path("/{newname}")
    public void renameLocation(Location loc, @PathParam("newname") String newName){
        c.getLog().log(Level.INFO, "LocationEndpoint: Rename location " + loc.name + " -> " + newName);
        handler.rename(loc, newName);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeLocation(Location loc){
        c.getLog().log(Level.INFO, "LocationEndpoint: Remove location " + loc.name);
        handler.remove(loc);
    }
}
