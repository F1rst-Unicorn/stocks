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

import de.njsm.stocks.server.v2.business.RecipeManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.FullRecipeForDeletion;
import de.njsm.stocks.server.v2.business.data.FullRecipeForInsertion;
import de.njsm.stocks.server.v2.business.data.Recipe;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeRecord;
import de.njsm.stocks.server.v2.web.data.Response;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("v2/recipe")
public class RecipeEndpoint extends Endpoint implements Get<RecipeRecord, Recipe>, JsonDelete<FullRecipeForDeletion, Recipe> {

    private final RecipeManager manager;

    @Inject
    public RecipeEndpoint(RecipeManager manager) {
        this.manager = manager;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response put(@Context HttpServletRequest request,
                        FullRecipeForInsertion input) {
        if (isValid(input)) {
            manager.setPrincipals(getPrincipals(request));
            StatusCode result = manager.add(input);
            return new Response(result);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @Override
    public RecipeManager getManager() {
        return manager;
    }
}
