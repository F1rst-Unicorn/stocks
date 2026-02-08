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
import de.njsm.stocks.server.v2.business.RecipeManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeRecord;
import fj.data.Validation;
import jakarta.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping("v2/recipe")
@RestController
@RequestScope
public class RecipeEndpoint extends Endpoint implements Get<RecipeRecord, Recipe>, JsonDelete<FullRecipeForDeletion, Recipe> {

    private final RecipeManager manager;

    public RecipeEndpoint(RecipeManager manager) {
        this.manager = manager;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON,produces = MediaType.APPLICATION_JSON)
    public Response put(@RequestBody FullRecipeForInsertion input) {
        Validation<StatusCode, Integer> result = manager.add(input);
        return new DataResponse<>(result);
    }

    @PutMapping(path = "edit", consumes = MediaType.APPLICATION_JSON,produces = MediaType.APPLICATION_JSON)
    public Response edit(@RequestBody FullRecipeForEditing input) {
        StatusCode result = manager.edit(input);
        return new Response(result);
    }

    @Override
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public Response delete(FullRecipeForDeletion input) {
        return JsonDelete.super.delete(input);
    }

    @Override
    public RecipeManager getManager() {
        return manager;
    }
}
