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
import de.njsm.stocks.server.v2.business.ScaledUnitManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.ScaledUnitRecord;
import fj.data.Validation;
import jakarta.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping("v2/scaled-unit")
@RestController
@RequestScope
public class ScaledUnitEndpoint extends Endpoint implements Get<ScaledUnitRecord, ScaledUnit>, Delete<ScaledUnitForDeletion, ScaledUnit> {

    private final ScaledUnitManager manager;

    public ScaledUnitEndpoint(ScaledUnitManager manager) {
        this.manager = manager;
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON)
    public Response put(@RequestParam("scale") String scale,
                        @RequestParam("unit") int unit) {
        Validation<StatusCode, Integer> status = manager.addReturningId(ScaledUnitForInsertion.builder()
                .scale(scale)
                .unit(unit)
                .build());
        return new DataResponse<>(status);
    }

    @PutMapping(path = "edit", produces = MediaType.APPLICATION_JSON)
    public Response edit(@RequestParam("id") int id,
                         @RequestParam("version") int version,
                         @RequestParam("scale") String scale,
                         @RequestParam("unit") int unit) {
        StatusCode status = manager.edit(ScaledUnitForEditing.builder()
                .id(id)
                .version(version)
                .scale(scale)
                .unit(unit)
                .build());
        return new Response(status);
    }

    @Override
    public ScaledUnitManager getManager() {
        return manager;
    }

    @Override
    public ScaledUnitForDeletion wrapParameters(int id, int version) {
        return ScaledUnitForDeletion.builder()
                .id(id)
                .version(version)
                .build();
    }
}
