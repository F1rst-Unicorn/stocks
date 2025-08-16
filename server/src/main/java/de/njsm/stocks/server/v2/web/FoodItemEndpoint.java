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
import de.njsm.stocks.common.api.serialisers.InstantDeserialiser;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.FoodItemManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodItemRecord;
import fj.data.Validation;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.inject.Inject;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.Instant;

@Path("v2/fooditem")
public class FoodItemEndpoint extends Endpoint implements
        Get<FoodItemRecord, FoodItem>,
        Delete<FoodItemForDeletion, FoodItem> {

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
                            @QueryParam("ofType") int ofType,
                            @QueryParam("unit") Integer unit) throws IOException {

        if (isValid(storedIn, "storedIn") &&
                isValid(ofType, "ofType") &&
                isValidInstant(expirationDate, "eatByDate")) {

            Instant eatByDate = InstantDeserialiser.parseString(expirationDate);
            Principals user = getPrincipals(request);
            manager.setPrincipals(user);
            Validation<StatusCode, Integer> status = manager.add(FoodItemForInsertion.builder()
                    .eatByDate(eatByDate)
                    .ofType(ofType)
                    .storedIn(storedIn)
                    .registers(user.getDid())
                    .buys(user.getUid())
                    .unit(unit)
                    .build());
            return new DataResponse<>(status);

        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PUT
    @Path("edit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editItem(@Context HttpServletRequest request,
                             @QueryParam("id") int id,
                             @QueryParam("version") int version,
                             @QueryParam("eatByDate") String expirationDate,
                             @QueryParam("storedIn") int storedIn,
                             @QueryParam("unit") Integer unit) throws IOException {
        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValidInstant(expirationDate, "eatByDate") &&
                isValid(storedIn, "storedIn")) {

            Instant eatByDate = InstantDeserialiser.parseString(expirationDate);
            Principals user = getPrincipals(request);
            manager.setPrincipals(user);
            StatusCode result = manager.edit(FoodItemForEditing.builder()
                    .id(id)
                    .version(version)
                    .eatBy(eatByDate)
                    .storedIn(storedIn)
                    .unit(unit)
                    .build());
            return new Response(result);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @Override
    public FoodItemManager getManager() {
        return manager;
    }

    @Override
    public FoodItemForDeletion wrapParameters(int id, int version) {
        return FoodItemForDeletion.builder()
                .id(id)
                .version(version)
                .build();
    }
}
