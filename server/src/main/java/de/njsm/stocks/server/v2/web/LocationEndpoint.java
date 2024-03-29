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


import de.njsm.stocks.common.api.Location;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.LocationForDeletion;
import de.njsm.stocks.common.api.LocationForInsertion;
import de.njsm.stocks.common.api.LocationForRenaming;
import de.njsm.stocks.common.api.LocationForSetDescription;
import de.njsm.stocks.server.v2.business.LocationManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.LocationRecord;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("v2/location")
public class LocationEndpoint extends Endpoint implements
        Get<LocationRecord, Location>,
        MetaDelete<LocationForDeletion, Location>{

    private final LocationManager manager;

    @Inject
    public LocationEndpoint(LocationManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putLocation(@Context HttpServletRequest request,
                                @QueryParam("name") String name) {
        if (isValid(name, "name")) {
            manager.setPrincipals(getPrincipals(request));
            StatusCode status = manager.put(LocationForInsertion.builder()
                    .name(name)
                    .build());
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PUT
    @Path("rename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response renameLocation(@Context HttpServletRequest request,
                                   @QueryParam("id") int id,
                                   @QueryParam("version") int version,
                                   @QueryParam("new") String newName) {

        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValid(newName, "new")) {
            manager.setPrincipals(getPrincipals(request));
            StatusCode status = manager.rename(LocationForRenaming.builder()
                    .id(id)
                    .version(version)
                    .name(newName)
                    .build());
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteLocation(@Context HttpServletRequest request,
                                   @QueryParam("id") int id,
                                   @QueryParam("version") int version,
                                   @QueryParam("cascade") int cascadeParameter) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {
            return delete(request, () -> LocationForDeletion.builder()
                    .id(id)
                    .version(version)
                    .cascade(cascadeParameter == 1)
                    .build());
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @POST
    @Path("description")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setDescription(@Context HttpServletRequest request,
                                   @QueryParam("id") int id,
                                   @QueryParam("version") int version,
                                   @FormParam("description") String description) {
        if (isValid(id, "id") && isValidVersion(version, "version") && isValidOrEmpty(description, "description")) {
            manager.setPrincipals(getPrincipals(request));
            StatusCode result = manager.setDescription(LocationForSetDescription.builder()
                    .id(id)
                    .version(version)
                    .description(description)
                    .build());
            return new Response(result);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @Override
    public LocationManager getManager() {
        return manager;
    }
}
