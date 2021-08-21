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


import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.business.FoodManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("v2/food")
public class FoodEndpoint extends Endpoint implements
        Get<FoodRecord, Food>,
        Delete<FoodForDeletion, Food> {

    private final FoodManager manager;

    @Inject
    public FoodEndpoint(FoodManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putFood(@Context HttpServletRequest request,
                            @QueryParam("name") String name,
                            @QueryParam("unit") Integer storeUnit) {
        if (isValid(name, "name")) {
            manager.setPrincipals(getPrincipals(request));
            StatusCode status = manager.add(FoodForInsertion.builder()
                    .name(name)
                    .storeUnit(storeUnit)
                    .build());
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PUT
    @Path("edit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response edit(@Context HttpServletRequest request,
                         @QueryParam("id") int id,
                         @QueryParam("version") int version,
                         @QueryParam("new") String newName,
                         @QueryParam("expirationoffset") Integer expirationOffset,
                         @QueryParam("location") Integer location,
                         @FormParam("description") String description,
                         @QueryParam("storeunit") Integer storeUnit) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValid(newName, "new")) {

            manager.setPrincipals(getPrincipals(request));
            StatusCode status = manager.rename(
                    FoodForEditing.builder()
                            .id(id)
                            .version(version)
                            .name(newName)
                            .expirationOffset(expirationOffset)
                            .location(location)
                            .description(description)
                            .storeUnit(storeUnit)
                            .build());
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PUT
    @Path("rename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response renameFood(@Context HttpServletRequest request,
                               @QueryParam("id") int id,
                               @QueryParam("version") int version,
                               @QueryParam("new") String newName,
                               @QueryParam("expirationoffset") Integer expirationOffset,
                               @QueryParam("location") Integer location,
                               @QueryParam("storeunit") Integer storeUnit) {
        return edit(request, id, version, newName, expirationOffset, location, null, storeUnit);
    }

    @PUT
    @Path("buy")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setToBuyStatus(@Context HttpServletRequest request,
                                   @QueryParam("id") int id,
                                   @QueryParam("version") int version,
                                   @QueryParam("buy") int toBuyParameter) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {

            manager.setPrincipals(getPrincipals(request));
            boolean toBuy = toBuyParameter == 1;
            StatusCode status = manager.setToBuyStatus(FoodForSetToBuy.builder()
                    .id(id)
                    .version(version)
                    .toBuy(toBuy)
                    .build());
            return new Response(status);
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
            StatusCode result = manager.setDescription(FoodForSetDescription.builder()
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
    public FoodManager getManager() {
        return manager;
    }

    @Override
    public FoodForDeletion wrapParameters(int id, int version) {
        return FoodForDeletion.builder()
                .id(id)
                .version(version)
                .build();
    }
}
