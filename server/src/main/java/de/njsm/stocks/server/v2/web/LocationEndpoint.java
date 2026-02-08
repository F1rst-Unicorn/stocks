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

import jakarta.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;
import de.njsm.stocks.common.api.Location;
import de.njsm.stocks.common.api.LocationForDeletion;
import de.njsm.stocks.common.api.LocationForInsertion;
import de.njsm.stocks.common.api.LocationForRenaming;
import de.njsm.stocks.common.api.LocationForSetDescription;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.business.LocationManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.LocationRecord;

@RequestMapping("/v2/location")
@RestController
@RequestScope
public class LocationEndpoint extends Endpoint implements
	Get<LocationRecord, Location>,
        MetaDelete<LocationForDeletion, Location> {

    private final LocationManager manager;

    public LocationEndpoint(LocationManager manager) {
        this.manager = manager;
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON)
    public Response putLocation(@RequestParam("name") String name) {
        if (isValid(name, "name")) {
            StatusCode status = manager.put(LocationForInsertion.builder()
                    .name(name)
                    .build());
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PutMapping(path = "rename", produces = MediaType.APPLICATION_JSON)
    public Response renameLocation(@RequestParam("id") int id,
                                   @RequestParam("version") int version,
                                   @RequestParam("new") String newName) {

        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValid(newName, "new")) {
            StatusCode status = manager.rename(LocationForRenaming.builder()
                    .id(id)
                    .version(version)
                    .name(newName)
                    .build());
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON)
    public Response deleteLocation(@RequestParam("id") int id,
                                   @RequestParam("version") int version,
                                   @RequestParam("cascade") int cascadeParameter) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {
            return delete(() -> LocationForDeletion.builder()
                    .id(id)
                    .version(version)
                    .cascade(cascadeParameter == 1)
                    .build());
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @PostMapping(
        path = "description",
        consumes = MediaType.APPLICATION_FORM_URLENCODED,
        produces = MediaType.APPLICATION_JSON)
    public Response setDescription(@RequestParam("id") int id,
                                   @RequestParam("version") int version,
                                   @RequestParam("description") String description) {
        if (isValid(id, "id") && isValidVersion(version, "version") && isValidOrEmpty(description, "description")) {
            StatusCode result = manager.setDescription(LocationForSetDescription.builder()
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
    public LocationManager getManager() {
        return manager;
    }
}
