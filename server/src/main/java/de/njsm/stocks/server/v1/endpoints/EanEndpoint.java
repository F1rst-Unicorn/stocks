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

package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.EanNumber;
import de.njsm.stocks.server.v1.internal.data.EanNumberFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/ean")
public class EanEndpoint extends Endpoint {

    public EanEndpoint(DatabaseHandler handler) {
        super(handler);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Data[] getEanNumbers(@Context HttpServletRequest request) {
        return handler.get(EanNumberFactory.f);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void addEanNumber(@Context HttpServletRequest request,
                             EanNumber numberToAdd) {
        numberToAdd.id = 0;
        handler.add(numberToAdd);
    }

    @PUT
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeEanNumber(@Context HttpServletRequest request,
                                EanNumber numberToRemove) {
        handler.remove(numberToRemove);
    }
}
