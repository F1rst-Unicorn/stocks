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
import de.njsm.stocks.server.v1.internal.data.Location;
import de.njsm.stocks.server.v1.internal.data.LocationFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;


@Path("/location")
public class LocationEndpoint extends Endpoint {

    public LocationEndpoint(DatabaseHandler handler) {
        super(handler);
    }

    @GET
    @Produces("application/json")
    public Data[] getLocations(@Context HttpServletRequest request) {
        return handler.get(LocationFactory.f);
    }

    @PUT
    @Consumes("application/json")
    public void addLocation(@Context HttpServletRequest request,
                            Location locationToAdd){
        locationToAdd.id = 0;
        handler.add(locationToAdd);
    }

    @PUT
    @Consumes("application/json")
    @Path("/{newname}")
    public void renameLocation(@Context HttpServletRequest request,
                               Location locationToRename,
                               @PathParam("newname") String newName){
        handler.rename(locationToRename, newName);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeLocation(@Context HttpServletRequest request,
                               Location locationToRemove){
        handler.remove(locationToRemove);
    }
}
