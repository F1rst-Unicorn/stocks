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

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.UserManager;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.web.data.DataResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import de.njsm.stocks.server.v2.web.data.StreamResponse;
import fj.data.Validation;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import java.util.stream.Stream;

@Path("/v2/user")
public class UserEndpoint extends Endpoint {

    private final UserManager manager;

    @Inject
    public UserEndpoint(UserManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces("application/json")
    public Response putUser(@QueryParam("name") String name) {

        if (isValidName(name, "name")) {
            StatusCode result = manager.addUser(new User(name));
            return new Response(result);

        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }

    @GET
    @Produces("application/json")
    public void get(@Suspended AsyncResponse response, @QueryParam("bitemporal") int bitemporalParameter) {
        boolean bitemporal = bitemporalParameter == 1;
        Validation<StatusCode, Stream<User>> result = manager.get(response, bitemporal);
        response.resume(new StreamResponse<>(result));
    }

    @DELETE
    @Produces("application/json")
    public Response deleteUser(@Context HttpServletRequest request,
                           @QueryParam("id") int id,
                           @QueryParam("version") int version) {

        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {
            StatusCode result = manager.deleteUser(new User(id, version), getPrincipals(request));
            return new Response(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }

    }

}
