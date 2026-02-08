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
import de.njsm.stocks.common.api.serialisers.InstantDeserialiser;
import de.njsm.stocks.server.v2.business.FoodItemManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodItemRecord;
import fj.data.Validation;
import jakarta.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.time.Instant;

@RequestMapping("v2/fooditem")
@RestController
@RequestScope
public class FoodItemEndpoint extends Endpoint implements
        Get<FoodItemRecord, FoodItem>,
        Delete<FoodItemForDeletion, FoodItem> {

    private final FoodItemManager manager;

    public FoodItemEndpoint(FoodItemManager manager) {
        this.manager = manager;
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON)
    public Response putItem(@RequestParam("eatByDate") String expirationDate,
                            @RequestParam("storedIn") int storedIn,
                            @RequestParam("ofType") int ofType,
                            @RequestParam("unit") Integer unit) throws IOException {

        if (isValid(storedIn, "storedIn") &&
                isValid(ofType, "ofType") &&
                isValidInstant(expirationDate, "eatByDate")) {

            Instant eatByDate = InstantDeserialiser.parseString(expirationDate);
            Validation<StatusCode, Integer> status = manager.add(FoodItemForInsertion.builder()
                    .eatByDate(eatByDate)
                    .ofType(ofType)
                    .storedIn(storedIn)
                    .registers(getPrincipals().getDid())
                    .buys(getPrincipals().getUid())
                    .unit(unit)
                    .build());
            return new DataResponse<>(status);

        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PutMapping(path = "edit", produces = MediaType.APPLICATION_JSON)
    public Response editItem(@RequestParam("id") int id,
                             @RequestParam("version") int version,
                             @RequestParam("eatByDate") String expirationDate,
                             @RequestParam("storedIn") int storedIn,
                             @RequestParam("unit") Integer unit) throws IOException {
        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValidInstant(expirationDate, "eatByDate") &&
                isValid(storedIn, "storedIn")) {

            Instant eatByDate = InstantDeserialiser.parseString(expirationDate);
            StatusCode result = manager.edit(FoodItemForEditing.builder()
                    .id(id)
                    .version(version)
                    .eatBy(eatByDate)
                    .storedIn(storedIn)
                    .unit(unit)
                    .build());
            return new Response(result);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @Override
    public FoodItemManager getManager() {
        return manager;
    }

    @Override
    public FoodItemForDeletion wrapParameters(int id, int version) {
        return FoodItemForDeletion.builder()
                .id(id)
                .version(version)
                .build();
    }
}
