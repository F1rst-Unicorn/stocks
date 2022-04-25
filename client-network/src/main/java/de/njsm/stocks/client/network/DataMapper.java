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

package de.njsm.stocks.client.network;

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.common.api.BitemporalLocation;
import de.njsm.stocks.common.api.BitemporalUser;
import de.njsm.stocks.common.api.LocationForEditing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DataMapper {

    private static final Logger LOG = LoggerFactory.getLogger(DataMapper.class);

    static Optional<Update> map(de.njsm.stocks.common.api.Update update) {
        return map(update.table())
                .map(v -> Update.create(v, update.lastUpdate()));
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

    public static UserForSynchronisation map(BitemporalUser source) {
        return UserForSynchronisation.builder()
                .id(source.id())
                .version(source.version())
                .validTimeStart(source.validTimeStart())
                .validTimeEnd(source.validTimeEnd())
                .transactionTimeStart(source.transactionTimeStart())
                .transactionTimeEnd(source.transactionTimeEnd())
                .initiates(source.initiates())
                .name(source.name())
                .build();
    }

    static Optional<EntityType> map(String entityType) {
        if (entityType.equalsIgnoreCase("location")) {
            return Optional.of(EntityType.LOCATION);
        } else if (entityType.equalsIgnoreCase("user")) {
            return Optional.of(EntityType.USER);
        }

        LOG.info("unknown entity type '" + entityType + "'");
        return Optional.empty();
    }

    static StatusCode map(de.njsm.stocks.common.api.StatusCode input) {
        return StatusCode.values()[input.ordinal()];
    }

    static LocationForEditing map(de.njsm.stocks.client.business.entities.LocationForEditing location) {
        return LocationForEditing.builder()
                .id(location.id())
                .version(location.version())
                .name(location.name())
                .description(location.description())
                .build();
    }
}
