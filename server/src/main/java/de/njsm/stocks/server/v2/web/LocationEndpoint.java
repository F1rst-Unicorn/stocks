package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.db.LocationHandler;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("v2/location")
public class LocationEndpoint extends Endpoint {

    private LocationHandler databaseHandler;

    public LocationEndpoint(LocationHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putLocation(@QueryParam("name") String name) {
        if (isValid(name, "name")) {
            StatusCode status = databaseHandler.add(new Location(name));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<Location> getLocation() {
        Validation<StatusCode, List<Location>> result = databaseHandler.get();
        return new ListResponse<>(result);
    }

    @PUT
    @Path("rename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response renameLocation(@QueryParam("id") int id,
                               @QueryParam("version") int version,
                               @QueryParam("new") String newName) {

        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValid(newName, "newName")) {
            StatusCode status = databaseHandler.rename(new Location(id, "", version), newName);
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteLocation(@QueryParam("id") int id,
                               @QueryParam("version") int version) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {
            StatusCode status = databaseHandler.delete(new Location(id, "", version));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }
}
