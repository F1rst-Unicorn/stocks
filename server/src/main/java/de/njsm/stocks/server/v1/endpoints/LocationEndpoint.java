package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.common.data.LocationFactory;
import de.njsm.stocks.server.util.UserContextFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;


@Path("/location")
public class LocationEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(LocationEndpoint.class);

    public LocationEndpoint(DatabaseHandler handler,
                            UserContextFactory contextFactory) {
        super(handler, contextFactory);
    }

    @GET
    @Produces("application/json")
    public Data[] getLocations(@Context HttpServletRequest request) {
        logAccess(LOG, request, "gets locations");
        return handler.get(LocationFactory.f);
    }

    @PUT
    @Consumes("application/json")
    public void addLocation(@Context HttpServletRequest request,
                            Location locationToAdd){
        logAccess(LOG, request, "adds location " + locationToAdd.name);
        handler.add(locationToAdd);
    }

    @PUT
    @Consumes("application/json")
    @Path("/{newname}")
    public void renameLocation(@Context HttpServletRequest request,
                               Location locationToRename,
                               @PathParam("newname") String newName){
        logAccess(LOG, request, "renames location "
                + locationToRename.name + " -> " + newName);
        handler.rename(locationToRename, newName);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeLocation(@Context HttpServletRequest request,
                               Location locationToRemove){
        logAccess(LOG, request, "removes location " + locationToRemove.name);
        handler.remove(locationToRemove);
    }
}
