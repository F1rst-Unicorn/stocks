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

import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.business.entities.LocationForSynchronisation;
import de.njsm.stocks.common.api.BitemporalLocation;
import de.njsm.stocks.common.api.StatusCode;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DataMapperTest {

    @Test
    public void statusCodesMapCorrectly() {
        for (StatusCode statusCode : StatusCode.values()) {
            de.njsm.stocks.client.business.entities.StatusCode mapped = DataMapper.map(statusCode);

            assertThat(mapped.toString(), is(statusCode.toString()));
            assertThat(mapped.ordinal(), is(statusCode.ordinal()));
        }
    }

    @Test
    void entityTypesMapCorrectly() {
        assertThat(DataMapper.map("Location"), is(Optional.of(EntityType.LOCATION)));
        assertThat(DataMapper.map("User"), is(Optional.of(EntityType.USER)));
        assertThat(DataMapper.map("User_device"), is(Optional.of(EntityType.USER_DEVICE)));
        assertThat(DataMapper.map("Food"), is(Optional.of(EntityType.FOOD)));
    }

    @Test
    void invalidEntityTypeIsMappedToEmpty() {
        assertFalse(DataMapper.map("unknown entity type").isPresent());
    }

    @Test
    void locationIsMappedCorrectly() {
        BitemporalLocation source = BitemporalLocation.builder()
                .id(1)
                .version(2)
                .validTimeStart(Instant.ofEpochMilli(3))
                .validTimeEnd(Instant.ofEpochMilli(4))
                .transactionTimeStart(Instant.ofEpochMilli(5))
                .transactionTimeEnd(Instant.ofEpochMilli(6))
                .initiates(7)
                .name("name")
                .description("description")
                .build();

        LocationForSynchronisation actual = DataMapper.map(source);

        assertThat(actual.id(), is(source.id()));
        assertThat(actual.version(), is(source.version()));
        assertThat(actual.validTimeStart(), is(source.validTimeStart()));
        assertThat(actual.validTimeEnd(), is(source.validTimeEnd()));
        assertThat(actual.transactionTimeStart(), is(source.transactionTimeStart()));
        assertThat(actual.transactionTimeEnd(), is(source.transactionTimeEnd()));
        assertThat(actual.transactionTimeEnd(), is(source.transactionTimeEnd()));
        assertThat(actual.initiates(), is(source.initiates()));
        assertThat(actual.name(), is(source.name()));
        assertThat(actual.description(), is(source.description()));
    }
}
