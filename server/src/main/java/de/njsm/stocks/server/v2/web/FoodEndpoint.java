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

import de.njsm.stocks.server.v2.business.FoodManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.*;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.time.Period;

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
            Validation<StatusCode, Integer> status = manager.add(new FoodForInsertion(name, storeUnit));
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
                    new FoodForEditing(
                            id,
                            version,
                            newName,
                            expirationOffset == null ? null : Period.ofDays(expirationOffset),
                            location,
                            description,
                            storeUnit));
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
            StatusCode status = manager.setToBuyStatus(new FoodForSetToBuy(id, version, toBuy));
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
            StatusCode result = manager.setDescription(new FoodForSetDescription(id, version, description));
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
        return new FoodForDeletion(id, version);
    }
}
