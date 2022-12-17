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
import de.njsm.stocks.client.business.event.UserDeviceEventFeedItem;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static de.njsm.stocks.client.business.Constants.INFINITY;
import static de.njsm.stocks.client.database.BitemporalOperations.currentDelete;
import static de.njsm.stocks.client.database.util.Util.testList;

public class EventRepositoryEventLoadingTest extends DbTestCase {

    private EventRepository uut;

    private UserDbEntity initiatorOwner;

    private UserDeviceDbEntity initiator;

    @Before
    public void setup() {
        uut = new EventRepositoryImpl(stocksDatabase.eventDao());
        initiatorOwner = standardEntities.userDbEntity();
        stocksDatabase.synchronisationDao().writeUsers(List.of(initiatorOwner));
        initiator = standardEntities.userDeviceDbEntityBuilder()
                .belongsTo(initiatorOwner.id())
                .build();
        stocksDatabase.synchronisationDao().writeUserDevices(List.of(initiator));
    }

    @Test
    public void gettingLocationEventsWorks() {
        var location = standardEntities.locationDbEntityBuilder()
                .initiates(initiator.id())
                .build();
        stocksDatabase.synchronisationDao().writeLocations(List.of(location));
        Instant updateTime = Instant.EPOCH.plusSeconds(1);
        stocksDatabase.synchronisationDao().writeLocations(BitemporalOperations.<LocationDbEntity, LocationDbEntity.Builder>
                currentUpdate(location, b -> b.name("newName"), updateTime));
        Instant deleteTime = Instant.EPOCH.plusSeconds(2);
        stocksDatabase.synchronisationDao().writeLocations(currentDelete(location, deleteTime));

        var actual = uut.getLocationFeed(Instant.EPOCH);

        testList(actual).assertValue(List.of(
                        LocationEventFeedItem.create(location.id(), deleteTime, deleteTime, initiatorOwner.name(), location.name(), location.description()),
                        LocationEventFeedItem.create(location.id(), updateTime, updateTime, initiatorOwner.name(), location.name(), location.description()),
                        LocationEventFeedItem.create(location.id(), INFINITY, updateTime, initiatorOwner.name(), "newName", location.description()),
                        LocationEventFeedItem.create(location.id(), INFINITY, Instant.EPOCH, initiatorOwner.name(), location.name(), location.description()))
                );
    }

    @Test
    public void gettingAddedDeviceEventWorks() {
        Instant inputDay = Instant.EPOCH.plus(1, ChronoUnit.DAYS);
        var newDeviceOwner = standardEntities.userDbEntityBuilder()
                .name("newDeviceOwner")
                .build();
        stocksDatabase.synchronisationDao().writeUsers(List.of(newDeviceOwner));
        var newDevice = standardEntities.userDeviceDbEntityBuilder()
                .belongsTo(newDeviceOwner.id())
                .initiates(initiator.id())
                .transactionTimeStart(inputDay)
                .build();
        stocksDatabase.synchronisationDao().writeUserDevices(List.of(newDevice));

        var actual = uut.getUserDeviceFeed(inputDay);

        testList(actual).assertValue(List.of(
                UserDeviceEventFeedItem.create(newDevice.id(),
                        newDevice.validTimeEnd(),
                        newDevice.transactionTimeStart(),
                        initiatorOwner.name(),
                        newDevice.name(),
                        newDeviceOwner.name())
        ));
    }
}
