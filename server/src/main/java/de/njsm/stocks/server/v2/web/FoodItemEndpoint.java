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

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.FoodItemManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.business.json.InstantDeserialiser;
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
import java.io.IOException;
import java.time.Instant;
import java.util.stream.Stream;

@Path("v2/fooditem")
public class FoodItemEndpoint extends Endpoint {

    private final FoodItemManager manager;

    @Inject
    public FoodItemEndpoint(FoodItemManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putItem(@Context HttpServletRequest request,
                            @QueryParam("eatByDate") String expirationDate,
                            @QueryParam("storedIn") int storedIn,
                            @QueryParam("ofType") int ofType) throws IOException {

        if (isValid(storedIn, "storedIn") &&
                isValid(ofType, "ofType") &&
                isValidInstant(expirationDate, "eatByDate")) {

            Instant eatByDate = InstantDeserialiser.parseString(expirationDate);
            Principals user = getPrincipals(request);
            Validation<StatusCode, Integer> status = manager.add(new FoodItem(eatByDate,
                    ofType, storedIn, user.getDid(), user.getUid()));
            return new Response(status);

        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void get(@Suspended AsyncResponse response, @QueryParam("bitemporal") int bitemporalParameter) {
        boolean bitemporal = bitemporalParameter == 1;
        Validation<StatusCode, Stream<FoodItem>> result = manager.get(response, bitemporal);
        response.resume(new StreamResponse<>(result));
    }

    @PUT
    @Path("edit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editItem(@Context HttpServletRequest request,
                             @QueryParam("id") int id,
                             @QueryParam("version") int version,
                             @QueryParam("eatByDate") String expirationDate,
                             @QueryParam("storedIn") int storedIn) throws IOException {
        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValidInstant(expirationDate, "eatByDate") &&
                isValid(storedIn, "storedIn")) {

            Instant eatByDate = InstantDeserialiser.parseString(expirationDate);
            Principals user = getPrincipals(request);
            StatusCode result = manager.edit(new FoodItem(id, version,
                    eatByDate, 0, storedIn, user.getDid(), user.getUid()));

            return new Response(result);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteItem(@QueryParam("id") int id,
                               @QueryParam("version") int version) {

        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {
            StatusCode status = manager.delete(new FoodItem(id, version));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }


}
