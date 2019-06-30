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

import de.njsm.stocks.server.v2.business.FoodManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("v2/food")
public class FoodEndpoint extends Endpoint {

    private FoodManager manager;

    public FoodEndpoint(FoodManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putFood(@QueryParam("name") String name) {
        if (isValid(name, "name")) {
            Validation<StatusCode, Integer> status = manager.add(new Food(name));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<Food> getFood() {
        Validation<StatusCode, List<Food>> result = manager.get();
        return new ListResponse<>(result);
    }

    @PUT
    @Path("rename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response renameFood(@QueryParam("id") int id,
                               @QueryParam("version") int version,
                               @QueryParam("new") String newName) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValid(newName, "new")) {

            StatusCode status = manager.rename(new Food(id, newName, version, false));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PUT
    @Path("buy")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setToBuyStatus(@QueryParam("id") int id,
                                   @QueryParam("version") int version,
                                   @QueryParam("buy") int toBuyParameter) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {

            boolean toBuy = toBuyParameter == 1;
            StatusCode status = manager.setToBuyStatus(new Food(id, "", version, toBuy));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFood(@QueryParam("id") int id,
                               @QueryParam("version") int version) {

        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {
            StatusCode status = manager.delete(new Food(id, "", version, false));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }


}
