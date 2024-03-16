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

package de.njsm.stocks.servertest.v2.repo;

import de.njsm.stocks.client.business.LocationAddService;
import de.njsm.stocks.client.business.UpdateService;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.entities.LocationAddForm;

import javax.inject.Inject;
import java.time.Instant;

public class LocationRepository {

    private final LocationAddService locationAddService;

    private final UpdateService updateService;

    @Inject
    public LocationRepository(LocationAddService locationAddService, UpdateService updateService) {
        this.locationAddService = locationAddService;
        this.updateService = updateService;
    }

    public IdImpl<Location> createNewLocationType(String name) {
        locationAddService.add(LocationAddForm.create(name, ""));
        return getIdOfLocation(name);
    }

    private IdImpl<Location> getIdOfLocation(String name) {
        var locations = updateService.getLocations(Instant.EPOCH);
        return locations.stream()
                .filter(v -> v.name().equals(name))
                .findFirst()
                .map(v -> IdImpl.<Location>create(v.id()))
                .orElseThrow();
    }
}
