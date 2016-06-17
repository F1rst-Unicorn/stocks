package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Location;

import javax.ws.rs.*;
import java.sql.SQLException;
import java.util.logging.Level;


@Path("/location")
public class LocationEndpoint extends Endpoint {

    @GET
    @Produces("application/json")
    public Location[] getLocations() {
        c.getLog().log(Level.INFO, "LocationEndpoint: Get locations");

        try {
            return handler.getLocations();
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "LocationEndpoint: Failed to get locations: " + e.getMessage());
        }
        return null;
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
        try {
            handler.removeLocation(loc.id);
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "LocationEndpoint: Failed to remove location: " + e.getMessage());
        }
    }
}
