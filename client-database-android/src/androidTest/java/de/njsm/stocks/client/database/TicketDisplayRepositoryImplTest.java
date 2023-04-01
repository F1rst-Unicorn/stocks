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
import de.njsm.stocks.client.business.entities.TicketDataForSharing;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.njsm.stocks.client.database.util.Util.test;

public class TicketDisplayRepositoryImplTest extends DbTestCase {

    private TicketDisplayRepositoryImpl uut;

    @Before
    public void setUp() {
        uut = new TicketDisplayRepositoryImpl(stocksDatabase.userDeviceDao());
    }

    @Test
    public void gettingDataWorks() {
        var user = standardEntities.userDbEntity();
        stocksDatabase.synchronisationDao().writeUsers(List.of(user));

        var userDevice = standardEntities.userDeviceDbEntityBuilder()
                .belongsTo(user.id())
                .build();
        stocksDatabase.synchronisationDao().writeUserDevices(List.of(userDevice));

        var ticket = standardEntities.ticketEntity(userDevice.id());
        stocksDatabase.userDeviceDao().store(ticket);

        var actual = uut.getRegistrationFormFor(userDevice::id);

        test(actual).assertValue(TicketDataForSharing.create(
                IdImpl.create(user.id()),
                user.name(),
                userDevice.name(),
                ticket.ticket()
        ));
    }
}
