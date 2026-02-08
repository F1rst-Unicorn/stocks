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
import de.njsm.stocks.server.v2.business.FoodManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping("v2/food")
@RestController
@RequestScope
public class FoodEndpoint extends Endpoint implements
        Get<FoodRecord, Food>,
        Delete<FoodForDeletion, Food> {

    private final FoodManager manager;

    public FoodEndpoint(FoodManager manager) {
        this.manager = manager;
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON)
    public Response putFood(@RequestParam("name") String name,
                            @RequestParam("unit") Integer storeUnit) {
        if (isValid(name, "name")) {
            StatusCode status = manager.add(FoodForInsertion.builder()
                    .name(name)
                    .storeUnit(storeUnit)
                    .build());
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PutMapping(path = "edit", consumes = MediaType. APPLICATION_FORM_URLENCODED, produces = MediaType.APPLICATION_JSON)
    public Response edit(@RequestParam("id") int id,
                         @RequestParam("version") int version,
                         @RequestParam("new") String newName,
                         @RequestParam("expirationoffset") Integer expirationOffset,
                         @RequestParam("location") Integer location,
                         @RequestParam("description") String description,
                         @RequestParam("storeunit") Integer storeUnit) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValid(newName, "new")) {

            StatusCode status = manager.rename(
                    FoodForEditing.builder()
                            .id(id)
                            .version(version)
                            .name(newName)
                            .expirationOffset(expirationOffset)
                            .location(location)
                            .description(description)
                            .storeUnit(storeUnit)
                            .build());
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PutMapping(path = "rename", produces = MediaType.APPLICATION_JSON)
    public Response renameFood(@RequestParam("id") int id,
                               @RequestParam("version") int version,
                               @RequestParam("new") String newName,
                               @RequestParam("expirationoffset") Integer expirationOffset,
                               @RequestParam("location") Integer location,
                               @RequestParam("storeunit") Integer storeUnit) {
        return edit(id, version, newName, expirationOffset, location, null, storeUnit);
    }

    @PutMapping(path = "buy", produces = MediaType.APPLICATION_JSON)
    public Response setToBuyStatus(@RequestParam("id") int id,
                                   @RequestParam("version") int version,
                                   @RequestParam("buy") int toBuyParameter) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {

            boolean toBuy = toBuyParameter == 1;
            StatusCode status = manager.setToBuyStatus(FoodForSetToBuy.builder()
                    .id(id)
                    .version(version)
                    .toBuy(toBuy)
                    .build());
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PostMapping(path = "description", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.APPLICATION_JSON)
    public Response setDescription(@RequestParam("id") int id,
                                   @RequestParam("version") int version,
                                   @RequestParam("description") String description) {
        if (isValid(id, "id") && isValidVersion(version, "version") && isValidOrEmpty(description, "description")) {
            StatusCode result = manager.setDescription(FoodForSetDescription.builder()
                    .id(id)
                    .version(version)
                    .description(description)
                    .build());
            return new Response(result);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @Override
    public FoodManager getManager() {
        return manager;
    }

    @Override
    public FoodForDeletion wrapParameters(int id, int version) {
        return FoodForDeletion.builder()
                .id(id)
                .version(version)
                .build();
    }
}
