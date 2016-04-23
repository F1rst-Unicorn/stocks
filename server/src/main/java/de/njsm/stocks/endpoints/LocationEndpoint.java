package de.njsm.stocks.endpoints;

import de.njsm.stocks.data.Location;
import de.njsm.stocks.internal.Config;

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
            c.getLog().log(Level.SEVERE, "LocationEndpoint: Failed to get locations: " + e.getSQLState());
        }
        return null;
    }

    @POST
    @Consumes("application/json")
    public void addLocation(Location loc){
        c.getLog().log(Level.INFO, "LocationEndpoint: Add location " + loc.name);
        try {
            handler.addLocation(loc);
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "LocationEndpoint: Failed to add location: " + e.getSQLState());
        }
    }

    @PUT
    @Consumes("application/json")
    @Path("/{newname}")
    public void renameLocation(Location loc, @PathParam("newname") String newName){
        c.getLog().log(Level.INFO, "LocationEndpoint: Rename location " + loc.name + " -> " + newName);
        try {
            handler.renameLocation(loc.id, newName);
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "LocationEndpoint: Failed to rename location: " + e.getSQLState());
        }
    }

    @PUT
    @Consumes("application/json")
    public void removeLocation(Location loc){
        c.getLog().log(Level.INFO, "LocationEndpoint: Remove location " + loc.name);
        try {
            handler.removeLocation(loc.id);
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "LocationEndpoint: Failed to remove location: " + e.getSQLState());
        }
    }
}
