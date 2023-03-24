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

import de.njsm.stocks.client.business.entities.UserDeviceForDeletion;
import de.njsm.stocks.client.business.entities.UserDeviceForListing;
import de.njsm.stocks.client.business.entities.UserDevicesForListing;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.njsm.stocks.client.database.util.Util.test;
import static org.junit.Assert.assertEquals;

public class UserDeviceListRepositoryImplTest extends DbTestCase {

    private UserDeviceListRepositoryImpl uut;

    @Before
    public void setUp() {
        uut = new UserDeviceListRepositoryImpl(stocksDatabase.userDeviceDao(), stocksDatabase.userDao());
    }

    @Test
    public void gettingUserDevicesWorks() {
        UserDbEntity user = standardEntities.userDbEntity();
        stocksDatabase.synchronisationDao().writeUsers(List.of(user));
        UserDeviceDbEntity device1 = standardEntities.userDeviceDbEntityBuilder()
                .name("first")
                .belongsTo(user.id())
                .build();
        UserDeviceDbEntity device2 = standardEntities.userDeviceDbEntityBuilder()
                .name("second")
                .belongsTo(user.id())
                .build();
        stocksDatabase.synchronisationDao().writeUserDevices(List.of(device1, device2));
        stocksDatabase.userDeviceDao().store(TicketEntity.create("ticket", device1.id()));

        var actual = uut.getUserDevices(user::id);

        test(actual).assertValue(UserDevicesForListing.create(
                List.of(
                        UserDeviceForListing.create(device1.id(), device1.name(), true),
                        UserDeviceForListing.create(device2.id(), device2.name(), false)
                ),
                user.name())
        );
    }

    @Test
    public void gettingSingleDeviceWorks() {
        UserDbEntity user = standardEntities.userDbEntity();
        stocksDatabase.synchronisationDao().writeUsers(List.of(user));
        UserDeviceDbEntity device = standardEntities.userDeviceDbEntityBuilder()
                .name("first")
                .belongsTo(user.id())
                .build();
        stocksDatabase.synchronisationDao().writeUserDevices(List.of(device));

        var actual = uut.getEntityForDeletion(device::id);

        assertEquals(UserDeviceForDeletion.create(
                device.id(),
                device.version()
        ), actual);
    }
}