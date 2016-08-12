package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Data;
import de.njsm.stocks.server.data.Location;
import de.njsm.stocks.server.data.LocationFactory;
import de.njsm.stocks.server.internal.auth.Principals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.logging.Level;


@Path("/location")
public class LocationEndpoint extends Endpoint {

    @GET
    @Produces("application/json")
    public Data[] getLocations(@Context HttpServletRequest request) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        c.getLog().log(Level.INFO, uc.getUsername() + "@" + uc.getDeviceName() + " gets locations");
        return handler.get(LocationFactory.f);
    }

    @PUT
    @Consumes("application/json")
    public void addLocation(@Context HttpServletRequest request, Location loc){
        Principals uc = c.getContextFactory().getPrincipals(request);
        c.getLog().log(Level.INFO, uc.getUsername() + "@" + uc.getDeviceName() + " adds location " + loc.name);
        handler.add(loc);
    }

    @PUT
    @Consumes("application/json")
    @Path("/{newname}")
    public void renameLocation(@Context HttpServletRequest request,
                               Location loc,
                               @PathParam("newname") String newName){

        Principals uc = c.getContextFactory().getPrincipals(request);
        c.getLog().log(Level.INFO, uc.getUsername() + "@" + uc.getDeviceName() + " renames location " +
                loc.name + " -> " + newName);
        handler.rename(loc, newName);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeLocation(@Context HttpServletRequest request, Location loc){
        Principals uc = c.getContextFactory().getPrincipals(request);
        c.getLog().log(Level.INFO, uc.getUsername() + "@" + uc.getDeviceName() + " removes location " + loc.name);
        handler.remove(loc);
    }
}
