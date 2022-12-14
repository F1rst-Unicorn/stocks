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

import de.njsm.stocks.client.business.event.EventRepository;
import de.njsm.stocks.client.business.event.LocationEventFeedItem;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.njsm.stocks.client.business.Constants.INFINITY;
import static de.njsm.stocks.client.database.BitemporalOperations.currentDelete;

public class EventRepositoryTest extends DbTestCase {

    private EventRepository uut;

    @Before
    public void setup() {
        uut = new EventRepositoryImpl(stocksDatabase.eventDao());
    }

    @Test
    public void gettingOldestEventWorks() {
        Instant expected = Instant.EPOCH.plusSeconds(2);
        var unit = standardEntities.unitDbEntityBuilder()
                .transactionTimeStart(expected)
                .build();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        var food = standardEntities.foodDbEntityBuilder()
                .transactionTimeStart(Instant.EPOCH.plusSeconds(3))
                .build();
        stocksDatabase.synchronisationDao().writeFood(List.of(food));

        var actual = uut.getOldestEventTime();

        actual.test().awaitDone(3, TimeUnit.SECONDS).assertValue(expected);
    }

    @Test
    public void gettingLocationEventsWorks() {
        var user = standardEntities.userDbEntity();
        stocksDatabase.synchronisationDao().writeUsers(List.of(user));
        var device = standardEntities.userDeviceDbEntityBuilder()
                .belongsTo(user.id())
                .build();
        stocksDatabase.synchronisationDao().writeUserDevices(List.of(device));
        var location = standardEntities.locationDbEntityBuilder()
                .initiates(device.id())
                .build();
        stocksDatabase.synchronisationDao().writeLocations(List.of(location));
        Instant updateTime = Instant.EPOCH.plusSeconds(1);
        stocksDatabase.synchronisationDao().writeLocations(BitemporalOperations.<LocationDbEntity, LocationDbEntity.Builder>
                currentUpdate(location, b -> b.name("newName"), updateTime));
        Instant deleteTime = Instant.EPOCH.plusSeconds(2);
        stocksDatabase.synchronisationDao().writeLocations(currentDelete(location, deleteTime));

        var actual = uut.getLocationFeed(Instant.EPOCH);

        actual.filter(v -> !v.isEmpty())
                .test()
                .awaitCount(1)
                .assertNoErrors()
                .assertValue(List.of(
                        LocationEventFeedItem.create(location.id(), deleteTime, deleteTime, user.name(), location.name(), location.description()),
                        LocationEventFeedItem.create(location.id(), updateTime, updateTime, user.name(), location.name(), location.description()),
                        LocationEventFeedItem.create(location.id(), INFINITY, updateTime, user.name(), "newName", location.description()),
                        LocationEventFeedItem.create(location.id(), INFINITY, Instant.EPOCH, user.name(), location.name(), location.description()))
                );
    }
}
