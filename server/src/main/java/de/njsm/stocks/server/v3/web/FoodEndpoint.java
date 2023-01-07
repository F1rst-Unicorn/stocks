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

package de.njsm.stocks.server.v3.web;


import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.business.FoodManager;
import de.njsm.stocks.server.v2.web.Endpoint;
import fj.data.Validation;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("v3/food")
public class FoodEndpoint extends Endpoint {

    private final FoodManager manager;

    @Inject
    public FoodEndpoint(FoodManager manager) {
        this.manager = manager;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DataResponse<Integer> put(@Context HttpServletRequest request,
                                     @NotNull FoodForInsertion food) {
        manager.setPrincipals(getPrincipals(request));
        Validation<StatusCode, Integer> status = manager.addReturningId(food);
        return new DataResponse<>(status);
    }

    @PUT
    @Path("edit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response edit(@Context HttpServletRequest request,
                         @NotNull FoodForFullEditing food) {
        manager.setPrincipals(getPrincipals(request));
        StatusCode status = manager.edit(food);
        return new Response(status);
    }
}
