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


import de.njsm.stocks.common.api.FoodItem;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.impl.FoodItemForDeletion;
import de.njsm.stocks.common.api.impl.FoodItemForEditing;
import de.njsm.stocks.common.api.impl.FoodItemForInsertion;
import de.njsm.stocks.common.api.serialisers.InstantDeserialiser;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.FoodItemManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodItemRecord;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
            StatusCode status = manager.add(new FoodItemForInsertion(eatByDate,
                    ofType, storedIn, user.getDid(), user.getUid(), unit));
            return new Response(status);

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
            StatusCode result = manager.edit(new FoodItemForEditing(id, version,
                    eatByDate, storedIn, unit));

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
        return new FoodItemForDeletion(id, version);
    }
}
