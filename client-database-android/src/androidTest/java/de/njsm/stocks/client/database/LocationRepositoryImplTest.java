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

import de.njsm.stocks.client.business.entities.LocationForListing;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.client.database.StocksDatabase.DATABASE_INFINITY;

public class LocationRepositoryImplTest extends DbTestCase {

    @Test
    public void gettingLocationsWorks() {
        List<LocationDbEntity> entities = Collections.singletonList(new LocationDbEntity(1, 2, Instant.EPOCH, DATABASE_INFINITY, Instant.EPOCH, DATABASE_INFINITY, 3, "name", "description"));
        LocationRepositoryImpl uut = new LocationRepositoryImpl(stocksDatabase.locationDao());
        stocksDatabase.synchronisationDao().synchroniseLocations(entities);
        List<LocationForListing> expected = entities.stream().map(DataMapper::map).collect(Collectors.toList());

        Observable<List<LocationForListing>> actual = uut.getLocations();

        actual.test().awaitCount(1).assertValue(expected);
    }
}
