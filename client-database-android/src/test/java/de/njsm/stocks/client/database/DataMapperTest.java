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
import de.njsm.stocks.client.business.entities.Update;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static de.njsm.stocks.client.database.DataMapper.map;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class DataMapperTest {

    private DataMapper uut;

    @Before
    public void setUp() {
        uut = new DataMapper();
    }

    @Test
    public void mappingUpdateWorks() {
        UpdateDbEntity input = new UpdateDbEntity(1, "location", Instant.MAX);

        Update actual = uut.map(input);

        assertThat(actual.table(), is(map(input.getTable())));
        assertThat(actual.lastUpdate(), is(input.getLastUpdate()));
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
