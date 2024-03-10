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


import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.Unit;
import de.njsm.stocks.common.api.UnitForDeletion;
import de.njsm.stocks.common.api.UnitForInsertion;
import de.njsm.stocks.common.api.UnitForRenaming;
import de.njsm.stocks.server.v2.business.UnitManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UnitRecord;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("v2/unit")
public class UnitEndpoint extends Endpoint implements Get<UnitRecord, Unit>, Delete<UnitForDeletion, Unit> {

    private final UnitManager manager;

    @Inject
    public UnitEndpoint(UnitManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response put(@Context HttpServletRequest request,
                        @QueryParam("name") String name,
                        @QueryParam("abbreviation") String abbreviation) {
        if (isValid(name, "name") && isValid(abbreviation, "abbreviation")) {
            manager.setPrincipals(getPrincipals(request));
            StatusCode status = manager.add(UnitForInsertion.builder()
                    .name(name)
                    .abbreviation(abbreviation)
                    .build());
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PUT
    @Path("rename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rename(@Context HttpServletRequest request,
                           @QueryParam("id") int id,
                           @QueryParam("version") int version,
                           @QueryParam("name") String name,
                           @QueryParam("abbreviation") String abbreviation) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValid(name, "new") &&
                isValid(abbreviation, "abbreviation")) {

            manager.setPrincipals(getPrincipals(request));
            StatusCode status = manager.rename(UnitForRenaming.builder()
                    .id(id)
                    .version(version)
                    .name(name)
                    .abbreviation(abbreviation)
                    .build());
            return new Response(status);

        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @Override
    public UnitManager getManager() {
        return manager;
    }

    @Override
    public UnitForDeletion wrapParameters(int id, int version) {
        return UnitForDeletion.builder()
                .id(id)
                .version(version)
                .build();
    }
}
