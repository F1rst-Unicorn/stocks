package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.LocationManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("v2/location")
public class LocationEndpoint extends Endpoint {

    private LocationManager locationManager;

    public LocationEndpoint(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putLocation(@QueryParam("name") String name) {
        if (isValid(name, "name")) {
            StatusCode status = locationManager.put(new Location(name));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<Location> getLocation() {
        Validation<StatusCode, List<Location>> result = locationManager.get();
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
            StatusCode status = locationManager.rename(new Location(id, "", version), newName);
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteLocation(@QueryParam("id") int id,
                                   @QueryParam("version") int version,
                                   @QueryParam("cascade") int cascadeParameter) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {

            boolean cascade = cascadeParameter == 1;
            StatusCode status = locationManager.delete(new Location(id, "", version), cascade);
            return new Response(status);

        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }
}
