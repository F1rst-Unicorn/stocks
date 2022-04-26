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
import org.junit.Test;

import java.time.Instant;

import static de.njsm.stocks.client.database.DataMapper.map;
import static de.njsm.stocks.client.database.StandardEntities.locationDbEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

public class DataMapperTest {

    @Test
    public void mappingUpdateWorks() {
        UpdateDbEntity input = UpdateDbEntity.create(EntityType.LOCATION, Instant.MAX);

        Update actual = map(input);

        assertThat(actual.table(), is(map(input.table())));
        assertThat(actual.lastUpdate(), is(input.lastUpdate()));
    }

    @Test
    public void mappingToUpdateDbEntityWorks() {
        Update input = Update.create(EntityType.LOCATION, Instant.MAX);

        UpdateDbEntity actual = DataMapper.map(input);

        assertThat(actual.lastUpdate(), is(input.lastUpdate()));
        assertThat(actual.table(), is(map(input.table())));
    }

    @Test
    public void mappingToLocationDbEntityWorks() {
        LocationForSynchronisation input = LocationForSynchronisation.builder()
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

        LocationDbEntity actual = DataMapper.map(input);

        assertEquals(input.id(), actual.id());
        assertEquals(input.version(), actual.version());
        assertEquals(input.validTimeStart(), actual.validTimeStart());
        assertEquals(input.validTimeEnd(), actual.validTimeEnd());
        assertEquals(input.transactionTimeStart(), actual.transactionTimeStart());
        assertEquals(input.transactionTimeEnd(), actual.transactionTimeEnd());
        assertEquals(input.initiates(), actual.initiates());
        assertEquals(input.name(), actual.name());
        assertEquals(input.description(), actual.description());
    }

    @Test
    public void mappingToLocationForListingWorks() {
        LocationDbEntity input = locationDbEntity();

        LocationForListing actual = DataMapper.map(input);

        assertEquals(input.id(), actual.id());
        assertEquals(input.name(), actual.name());
    }
}
