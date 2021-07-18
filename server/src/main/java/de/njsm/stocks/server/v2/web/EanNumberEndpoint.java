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
import de.njsm.stocks.server.v2.db.jooq.tables.records.EanNumberRecord;
import de.njsm.stocks.server.v2.web.data.Response;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
            StatusCode status = manager.add(new EanNumberForInsertion(foodId, code));
            return new Response(status);
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
        return new EanNumberForDeletion(id, version);
    }
}
