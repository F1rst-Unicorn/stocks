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
import de.njsm.stocks.server.v2.business.data.Food;
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
import java.time.Period;
import java.util.Optional;
import java.util.stream.Stream;

@Path("v2/food")
public class FoodEndpoint extends Endpoint {

    private final FoodManager manager;

    @Inject
    public FoodEndpoint(FoodManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putFood(@Context HttpServletRequest request,
                            @QueryParam("name") String name) {
        if (isValid(name, "name")) {
            manager.setPrincipals(getPrincipals(request));
            Validation<StatusCode, Integer> status = manager.add(new Food(name));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
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

            Validation<StatusCode, Stream<Food>> result = manager.get(response, bitemporal, startingFrom.get());
            response.resume(new StreamResponse<>(result));
        } else {
            response.resume(new Response(StatusCode.INVALID_ARGUMENT));
        }
    }

    @PUT
    @Path("rename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response renameFood(@Context HttpServletRequest request,
                               @QueryParam("id") int id,
                               @QueryParam("version") int version,
                               @QueryParam("new") String newName,
                               @QueryParam("expirationoffset") int expirationOffset,
                               @QueryParam("location") int location) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValid(newName, "new")) {

            manager.setPrincipals(getPrincipals(request));
            StatusCode status = manager.rename(new Food(id, newName, version, false, Period.ofDays(expirationOffset), location == 0 ? null : location, ""));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
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
            StatusCode status = manager.setToBuyStatus(new Food(id, "", version, toBuy, Period.ZERO, 0, ""));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFood(@Context HttpServletRequest request,
                               @QueryParam("id") int id,
                               @QueryParam("version") int version) {

        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {
            manager.setPrincipals(getPrincipals(request));
            StatusCode status = manager.delete(new Food(id, "", version, false, Period.ZERO, 0, ""));
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
        if (isValid(id, "id") && isValidVersion(version, "version")) {
            manager.setPrincipals(getPrincipals(request));
            manager.setDescription(new Food(id, version, description));
            return new Response(StatusCode.SUCCESS);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }
}
