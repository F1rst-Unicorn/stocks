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

import de.njsm.stocks.server.v2.business.EanNumberManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.business.data.EanNumberForDeletion;
import de.njsm.stocks.server.v2.business.data.EanNumberForInsertion;
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

@Path("v2/ean")
public class EanNumberEndpoint extends Endpoint {

    private final EanNumberManager manager;

    @Inject
    public EanNumberEndpoint(EanNumberManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putEanNumber(@Context HttpServletRequest request,
                                 @QueryParam("code") String code,
                                 @QueryParam("identifies") int foodId) {
        if (isValid(code, "code") &&
                isValid(foodId, "foodId")) {

            manager.setPrincipals(getPrincipals(request));
            Validation<StatusCode, Integer> status = manager.add(new EanNumberForInsertion(foodId, code));
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
            Validation<StatusCode, Stream<EanNumber>> result = manager.get(response, bitemporal, startingFrom.get());
            response.resume(new StreamResponse<>(result));
        } else {
            response.resume(new Response(StatusCode.INVALID_ARGUMENT));
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEanNumber(@Context HttpServletRequest request,
                                    @QueryParam("id") int id,
                                    @QueryParam("version") int version) {

        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {

            manager.setPrincipals(getPrincipals(request));
            StatusCode status = manager.delete(new EanNumberForDeletion(id, version));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }


}
