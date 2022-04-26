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

import de.njsm.stocks.client.business.entities.Bitemporal;
import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.business.entities.Update;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.LocationForEditing;
import de.njsm.stocks.common.api.*;
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
        return map(source, LocationForSynchronisation.builder())
                .name(source.name())
                .description(source.description())
                .build();
    }

    public static UserForSynchronisation map(BitemporalUser source) {
        return map(source, UserForSynchronisation.builder())
                .name(source.name())
                .build();
    }

    public static UserDeviceForSynchronisation map(BitemporalUserDevice source) {
        return map(source, UserDeviceForSynchronisation.builder())
                .name(source.name())
                .belongsTo(source.belongsTo())
                .build();
    }

    public static FoodForSynchronisation map(BitemporalFood source) {
        return map(source, FoodForSynchronisation.builder())
                .name(source.name())
                .toBuy(source.toBuy())
                .expirationOffset(source.expirationOffset())
                .location(source.location())
                .storeUnit(source.storeUnit())
                .description(source.description())
                .build();
    }

    private static <T extends Bitemporal.Builder<T>, E extends Entity<E>> T map(de.njsm.stocks.common.api.Bitemporal<E> source, T destination) {
        return destination
                .id(source.id())
                .version(source.version())
                .validTimeStart(source.validTimeStart())
                .validTimeEnd(source.validTimeEnd())
                .transactionTimeStart(source.transactionTimeStart())
                .transactionTimeEnd(source.transactionTimeEnd())
                .initiates(source.initiates());
    }

    static Optional<EntityType> map(String entityType) {
        if (entityType.equalsIgnoreCase("location")) {
            return Optional.of(EntityType.LOCATION);
        } else if (entityType.equalsIgnoreCase("user")) {
            return Optional.of(EntityType.USER);
        } else if (entityType.equalsIgnoreCase("user_device")) {
            return Optional.of(EntityType.USER_DEVICE);
        } else if (entityType.equalsIgnoreCase("food")) {
            return Optional.of(EntityType.FOOD);
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
