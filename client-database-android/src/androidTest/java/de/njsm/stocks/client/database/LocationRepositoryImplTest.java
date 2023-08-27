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

import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.LocationEditFormData;
import de.njsm.stocks.client.business.entities.LocationForListing;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LocationRepositoryImplTest extends DbTestCase {

    private LocationRepositoryImpl uut;

    @Before
    public void setUp() {
        uut = new LocationRepositoryImpl(stocksDatabase.locationDao());
    }

    @Test
    public void gettingLocationsWorks() {
        List<LocationDbEntity> entities = Collections.singletonList(standardEntities.locationDbEntity());
        stocksDatabase.synchronisationDao().synchroniseLocations(entities);
        List<LocationForListing> expected = entities.stream().map(DataMapper::map).collect(Collectors.toList());

        Observable<List<LocationForListing>> actual = uut.getLocations();

        actual.test().awaitCount(1).assertValue(expected);
    }

    @Test
    public void gettingLocationForEditingWorks() {
        LocationDbEntity location = standardEntities.locationDbEntity();
        stocksDatabase.synchronisationDao().synchroniseLocations(Collections.singletonList(location));
        LocationEditFormData expected = DataMapper.mapToEdit(location);

        Observable<LocationEditFormData> actual = uut.getLocationForEditing(IdImpl.create(location.id()));

        actual.test().awaitCount(1).assertValue(expected);
    }
}
