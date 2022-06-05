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

package de.njsm.stocks.client.database.error;

import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.entities.ErrorDetails;
import de.njsm.stocks.client.business.entities.LocationEditErrorDetails;
import de.njsm.stocks.client.business.entities.LocationForEditing;
import de.njsm.stocks.client.database.LocationDbEntity;
import de.njsm.stocks.client.database.StandardEntities;

import java.util.List;

import static java.util.Collections.singletonList;

public class LocationEditErrorRepositoryImplTest extends AbstractErrorRepositoryImplTest {

    ErrorDetails recordError(StatusCodeException e) {
        LocationDbEntity location = StandardEntities.locationDbEntity();
        LocationForEditing locationForEditing = LocationForEditing.builder()
                .id(location.id())
                .version(location.version())
                .name(location.name())
                .description(location.description())
                .build();
        stocksDatabase.synchronisationDao().writeLocations(singletonList(location));
        errorRecorder.recordLocationEditError(e, locationForEditing);
        return LocationEditErrorDetails.create(location.id(), location.name(), location.description());
    }

    @Override
    List<?> getErrorDetails() {
        return stocksDatabase.errorDao().getLocationEdits();
    }
}
