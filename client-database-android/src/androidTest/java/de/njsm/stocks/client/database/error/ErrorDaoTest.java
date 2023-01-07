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

import de.njsm.stocks.client.database.DbTestCase;
import de.njsm.stocks.client.database.LocationDbEntity;
import org.junit.Before;
import org.junit.Test;

import static java.time.Instant.EPOCH;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ErrorDaoTest extends DbTestCase {

    private ErrorDao uut;

    @Before
    public void setup() {
        uut = stocksDatabase.errorDao();
    }

    @Test
    public void locationIsLoadableAtPointInTransactionTimeWhenTwoLocationsPresent() {
        LocationDbEntity location1 = standardEntities.locationDbEntity();
        LocationDbEntity location2 = standardEntities.locationDbEntityBuilder()
                .validTimeStart(EPOCH.plusSeconds(1))
                .build();
        stocksDatabase.synchronisationDao().writeLocations(asList(location1, location2));

        LocationDbEntity actual = uut.getCurrentLocationAsKnownAt(location1.id(), location2.validTimeStart());

        assertEquals(location1, actual);
    }

    @Test
    public void locationIsLoadableAsBestKnownWhenTwoLocationsPresent() {
        LocationDbEntity location1 = standardEntities.locationDbEntity();
        LocationDbEntity location2 = standardEntities.locationDbEntityBuilder()
                .validTimeStart(EPOCH.plusSeconds(1))
                .build();
        stocksDatabase.synchronisationDao().writeLocations(asList(location1, location2));

        LocationDbEntity actual = uut.getLatestLocationAsBestKnown(location1.id());

        assertEquals(location1, actual);
    }
}
