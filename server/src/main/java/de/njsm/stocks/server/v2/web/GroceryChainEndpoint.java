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
import de.njsm.stocks.server.v2.business.GroceryChainManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.GroceryChainRecord;
import fj.data.Validation;
import jakarta.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping("v2/grocery-chain")
@RestController
@RequestScope
public class GroceryChainEndpoint extends Endpoint implements
        Get<GroceryChainRecord, GroceryChain>,
        Delete<GroceryChainForDeletion, GroceryChain> {

    private final GroceryChainManager groceryChainManager;

    public GroceryChainEndpoint(GroceryChainManager groceryChainManager) {
        this.groceryChainManager = groceryChainManager;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public DataResponse<Integer> put(@RequestBody GroceryChainForInsertion data) {
        Validation<StatusCode, Integer> status = groceryChainManager.addReturningId(data);
        return new DataResponse<>(status);
    }

    @PutMapping(path = "edit", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public Response edit(@RequestBody GroceryChainForEditing groceryChain) {
        StatusCode status = groceryChainManager.edit(groceryChain);
        return new Response(status);
    }

    @Override
    public GroceryChainManager getManager() {
        return groceryChainManager;
    }

    @Override
    public GroceryChainForDeletion wrapParameters(int id, int version) {
        return GroceryChainForDeletion.builder()
                .id(id)
                .version(version)
                .build();
    }
}
