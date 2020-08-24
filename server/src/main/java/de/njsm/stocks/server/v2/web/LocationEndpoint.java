/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.LocationManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.web.data.Response;
import de.njsm.stocks.server.v2.web.data.StreamResponse;
import fj.data.Validation;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.util.stream.Stream;

@Path("v2/location")
public class LocationEndpoint extends Endpoint {

    private final LocationManager manager;

    @Inject
    public LocationEndpoint(LocationManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putLocation(@QueryParam("name") String name) {
        if (isValid(name, "name")) {
            StatusCode status = manager.put(new Location(name));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void get(@Suspended AsyncResponse response, @QueryParam("bitemporal") int bitemporalParameter) {
        boolean bitemporal = bitemporalParameter == 1;
        Validation<StatusCode, Stream<Location>> result = manager.get(response, bitemporal);
        response.resume(new StreamResponse<>(result));
    }

    @PUT
    @Path("rename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response renameLocation(@QueryParam("id") int id,
                               @QueryParam("version") int version,
                               @QueryParam("new") String newName) {

        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValid(newName, "new")) {
            StatusCode status = manager.rename(new Location(id, newName, version));
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
            StatusCode status = manager.delete(new Location(id, "", version), cascade);
            return new Response(status);

        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }
}
