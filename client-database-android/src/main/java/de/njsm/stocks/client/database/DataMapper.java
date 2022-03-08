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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.business.entities.LocationForListing;
import de.njsm.stocks.client.business.entities.LocationForSynchronisation;
import de.njsm.stocks.client.business.entities.Update;

class DataMapper {

    static Update map(UpdateDbEntity input) {
        return Update.create(map(input.table()), input.lastUpdate());
    }

    static UpdateDbEntity map(Update input) {
        return UpdateDbEntity.create(map(input.table()), input.lastUpdate());
    }

    static LocationDbEntity map(LocationForSynchronisation location) {
        return new LocationDbEntity(
                location.id(),
                location.version(),
                location.validTimeStart(),
                location.validTimeEnd(),
                location.transactionTimeStart(),
                location.transactionTimeEnd(),
                location.initiates(),
                location.name(),
                location.description()
        );
    }

    static LocationForListing map(LocationDbEntity input) {
        return LocationForListing.create(input.getId(), input.getName());
    }

    static EntityType map(String entityType) {
        if (entityType.equalsIgnoreCase("location")) {
            return EntityType.LOCATION;
        }

        throw new IllegalArgumentException("invalid entity type '" + entityType + "'");
    }

    static String map(EntityType entityType) {
        return entityType.name().toLowerCase();
    }
}
