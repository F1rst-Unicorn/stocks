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

package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.business.entities.LocationForSynchronisation;
import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.business.entities.Update;
import de.njsm.stocks.common.api.BitemporalLocation;

public class DataMapper {

    static Update map(de.njsm.stocks.common.api.Update update) {
        return Update.create(map(update.table()), update.lastUpdate());
    }

    public static LocationForSynchronisation map(BitemporalLocation source) {
        return LocationForSynchronisation.builder()
                .id(source.id())
                .version(source.version())
                .validTimeStart(source.validTimeStart())
                .validTimeEnd(source.validTimeEnd())
                .transactionTimeStart(source.transactionTimeStart())
                .transactionTimeEnd(source.transactionTimeEnd())
                .initiates(source.initiates())
                .name(source.name())
                .description(source.description())
                .build();
    }

    static EntityType map(String entityType) {
        if (entityType.equalsIgnoreCase("location")) {
            return EntityType.LOCATION;
        }

        throw new IllegalArgumentException("invalid entity type '" + entityType + "'");
    }

    static StatusCode map(de.njsm.stocks.common.api.StatusCode input) {
        return StatusCode.values()[input.ordinal()];
    }
}
