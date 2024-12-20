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
import de.njsm.stocks.server.v2.business.ScaledUnitManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.ScaledUnitRecord;
import fj.data.Validation;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("v2/scaled-unit")
public class ScaledUnitEndpoint extends Endpoint implements Get<ScaledUnitRecord, ScaledUnit>, Delete<ScaledUnitForDeletion, ScaledUnit> {

    private final ScaledUnitManager manager;

    @Inject
    public ScaledUnitEndpoint(ScaledUnitManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response put(@Context HttpServletRequest request,
                        @QueryParam("scale") String scale,
                        @QueryParam("unit") int unit) {
        manager.setPrincipals(getPrincipals(request));
        Validation<StatusCode, Integer> status = manager.addReturningId(ScaledUnitForInsertion.builder()
                .scale(scale)
                .unit(unit)
                .build());
        return new DataResponse<>(status);
    }

    @PUT
    @Path("edit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response edit(@Context HttpServletRequest request,
                         @QueryParam("id") int id,
                         @QueryParam("version") int version,
                         @QueryParam("scale") String scale,
                         @QueryParam("unit") int unit) {
        manager.setPrincipals(getPrincipals(request));
        StatusCode status = manager.edit(ScaledUnitForEditing.builder()
                .id(id)
                .version(version)
                .scale(scale)
                .unit(unit)
                .build());
        return new Response(status);
    }

    @Override
    public ScaledUnitManager getManager() {
        return manager;
    }

    @Override
    public ScaledUnitForDeletion wrapParameters(int id, int version) {
        return ScaledUnitForDeletion.builder()
                .id(id)
                .version(version)
                .build();
    }
}
