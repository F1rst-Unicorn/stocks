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
import static de.njsm.stocks.client.database.StocksDatabase.DATABASE_INFINITY;
import static java.time.Instant.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class DataMapperTest {

    @Test
    public void mappingUpdateWorks() {
        UpdateDbEntity input = UpdateDbEntity.create("location", Instant.MAX);

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

        assertEquals(input.id(), actual.getId());
        assertEquals(input.version(), actual.getVersion());
        assertEquals(input.validTimeStart(), actual.getValidTimeStart());
        assertEquals(input.validTimeEnd(), actual.getValidTimeEnd());
        assertEquals(input.transactionTimeStart(), actual.getTransactionTimeStart());
        assertEquals(input.transactionTimeEnd(), actual.getTransactionTimeEnd());
        assertEquals(input.initiates(), actual.getInitiates());
        assertEquals(input.name(), actual.getName());
        assertEquals(input.description(), actual.getDescription());
    }

    @Test
    public void mappingToLocationForListingWorks() {
        LocationDbEntity input = new LocationDbEntity(1, 2, now(), DATABASE_INFINITY, now(), DATABASE_INFINITY, 3, "name", "description");

        LocationForListing actual = DataMapper.map(input);

        assertEquals(input.getId(), actual.id());
        assertEquals(input.getName(), actual.name());
    }

    @Test
    public void entityTypesMapCorrectly() {
        assertThat(map("Location"), is(EntityType.LOCATION));
    }

    @Test
    public void invalidEntityTypeThrows() {
        assertThrows(IllegalArgumentException.class, () -> map("unknown entity type"));
    }
}
