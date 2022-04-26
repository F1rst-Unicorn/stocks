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
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SynchronisationDaoTest extends DbTestCase {

    private SynchronisationDao uut;

    @Before
    public void setUp() {
        uut = stocksDatabase.synchronisationDao();
    }

    @Test
    public void insertingWorks() {
        UpdateDbEntity input = UpdateDbEntity.create(EntityType.LOCATION, Instant.EPOCH);
        UpdateDbEntity expected = UpdateDbEntity.create(1, input.table(), input.lastUpdate());

        uut.insert(singletonList(input));

        List<UpdateDbEntity> actual = uut.getAll();
        assertThat(actual, is(singletonList(expected)));
    }

    @Test
    public void synchronisingDeletesOldContent() {
        UpdateDbEntity oldContent = UpdateDbEntity.create(EntityType.LOCATION, Instant.EPOCH);
        uut.insert(singletonList(oldContent));
        UpdateDbEntity input = UpdateDbEntity.create(EntityType.USER, Instant.EPOCH);
        UpdateDbEntity expected = UpdateDbEntity.create(2, input.table(), input.lastUpdate());

        uut.writeUpdates(singletonList(input));

        List<UpdateDbEntity> actual = uut.getAll();
        assertThat(actual, is(singletonList(expected)));
    }
}
