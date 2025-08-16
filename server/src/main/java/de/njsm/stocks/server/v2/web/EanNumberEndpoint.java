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
import de.njsm.stocks.server.v2.business.EanNumberManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.EanNumberRecord;
import fj.data.Validation;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.inject.Inject;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Path("v2/ean")
public class EanNumberEndpoint extends Endpoint implements
        Get<EanNumberRecord, EanNumber>,
        Delete<EanNumberForDeletion, EanNumber> {

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
            Validation<StatusCode, Integer> status = manager.addReturningId(EanNumberForInsertion.builder()
                    .identifiesFood(foodId)
                    .eanNumber(code)
                    .build());
            return new DataResponse<>(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @Override
    public EanNumberManager getManager() {
        return manager;
    }

    @Override
    public EanNumberForDeletion wrapParameters(int id, int version) {
        return EanNumberForDeletion.builder()
                .id(id)
                .version(version)
                .build();
    }
}
