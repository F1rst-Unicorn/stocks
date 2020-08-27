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

import de.njsm.stocks.server.v2.business.DeviceManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.NewDeviceTicket;
import de.njsm.stocks.server.v2.business.data.UserDevice;
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
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

@Path("v2/device")
public class DeviceEndpoint extends Endpoint {

    private final DeviceManager manager;

    @Inject
    public DeviceEndpoint(DeviceManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public DataResponse<NewDeviceTicket> putDevice(@QueryParam("name") String name,
                                                @QueryParam("belongsTo") int userId) {
        if (isValidName(name, "name") &&
                isValid(userId, "userId")) {

            Validation<StatusCode, NewDeviceTicket> result = manager.addDevice(new UserDevice(name, userId));
            return new DataResponse<>(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void get(@Suspended AsyncResponse response,
                    @QueryParam("bitemporal") int bitemporalParameter,
                    @QueryParam("startingFrom") String startingFromParameter) {
        boolean bitemporal = bitemporalParameter == 1;
        Optional<Instant> startingFrom = parseToInstant(startingFromParameter, "startingFrom");
        if (startingFrom.isPresent()) {
            Validation<StatusCode, Stream<UserDevice>> result = manager.get(response, bitemporal, startingFrom.get());
            response.resume(new StreamResponse<>(result));
        } else {
            response.resume(new Response(StatusCode.INVALID_ARGUMENT));
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDevice(@Context HttpServletRequest request,
                                 @QueryParam("id") int id,
                                 @QueryParam("version") int version) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {

            StatusCode result = manager.removeDevice(new UserDevice(id, version), getPrincipals(request));
            return new Response(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }

    @DELETE
    @Path("/revoke")
    @Produces(MediaType.APPLICATION_JSON)
    public Response revokeDevice(@Context HttpServletRequest request,
                                 @QueryParam("id") int id,
                                 @QueryParam("version") int version) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {

            StatusCode result = manager.revokeDevice(new UserDevice(id, version));
            return new Response(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }
}
