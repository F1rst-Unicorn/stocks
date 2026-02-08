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
import de.njsm.stocks.server.v2.business.UnitManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UnitRecord;
import fj.data.Validation;
import jakarta.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping("v2/unit")
@RestController
@RequestScope
public class UnitEndpoint extends Endpoint implements Get<UnitRecord, Unit>, Delete<UnitForDeletion, Unit> {

    private final UnitManager manager;

    public UnitEndpoint(UnitManager manager) {
        this.manager = manager;
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON)
    public Response put(@RequestParam("name") String name,
                                     @RequestParam("abbreviation") String abbreviation) {
        if (isValid(name, "name") && isValid(abbreviation, "abbreviation")) {
            Validation<StatusCode, Integer> status = manager.addReturningId(UnitForInsertion.builder()
                    .name(name)
                    .abbreviation(abbreviation)
                    .build());
            return new DataResponse<>(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PutMapping(path = "rename", produces = MediaType.APPLICATION_JSON)
    public Response rename(@RequestParam("id") int id,
                           @RequestParam("version") int version,
                           @RequestParam("name") String name,
                           @RequestParam("abbreviation") String abbreviation) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValid(name, "new") &&
                isValid(abbreviation, "abbreviation")) {

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
