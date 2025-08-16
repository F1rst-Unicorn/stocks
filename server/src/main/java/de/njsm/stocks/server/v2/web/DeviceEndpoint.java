/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.business.BusinessGettable;
import de.njsm.stocks.server.v2.business.DeviceManager;
import de.njsm.stocks.server.v2.business.data.NewDeviceTicket;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserDeviceRecord;
import fj.data.Validation;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Path("v2/device")
public class DeviceEndpoint extends Endpoint implements Get<UserDeviceRecord, UserDevice> {

    private final DeviceManager manager;

    @Inject
    public DeviceEndpoint(DeviceManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public DataResponse<NewDeviceTicket> putDevice(
            @Context HttpServletRequest request,
            @QueryParam("name") String name,
            @QueryParam("belongsTo") int userId) {

        if (isValidName(name, "name") &&
                isValid(userId, "userId")) {
            manager.setPrincipals(getPrincipals(request));
            Validation<StatusCode, NewDeviceTicket> result = manager.addDevice(UserDeviceForInsertion.builder()
                    .name(name)
                    .belongsTo(userId)
                    .build());
            return new DataResponse<>(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDevice(@Context HttpServletRequest request,
                                 @QueryParam("id") int id,
                                 @QueryParam("version") int version) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {

            manager.setPrincipals(getPrincipals(request));
            StatusCode result = manager.delete(UserDeviceForDeletion.builder()
                    .id(id)
                    .version(version)
                    .build());
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

            manager.setPrincipals(getPrincipals(request));
            StatusCode result = manager.revokeDevice(UserDeviceForDeletion.builder()
                    .id(id)
                    .version(version)
                    .build());
            return new Response(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }

    @Override
    public BusinessGettable<UserDeviceRecord, UserDevice> getManager() {
        return manager;
    }
}
