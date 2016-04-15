package de.njsm.stocks.endpoints;

import de.njsm.stocks.data.Location;

import javax.ws.rs.*;
import java.sql.SQLException;


@Path("/location")
public class LocationEndpoint extends Endpoint {

    @GET
    @Produces("application/json")
    public Location[] getLocations() {

        try {
            return handler.getLocations();
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @POST
    @Consumes("application/json")
    public void addLocation(Location loc){

        try {
            handler.addLocation(loc);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @PUT
    @Consumes("application/json")
    @Path("/{newname}")
    public void renameLocation(Location loc, @PathParam("newname") String newName){

        try {
            handler.renameLocation(loc.id, newName);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @PUT
    @Consumes("application/json")
    public void removeLocation(Location loc){

        try {
            handler.removeLocation(loc.id);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
