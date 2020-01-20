/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import de.njsm.stocks.server.v2.business.data.ServerTicket;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TicketBackendTest extends DbTestCase {

    private TicketBackend uut;

    @Before
    public void setup() {
        uut = new TicketBackend(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
    }

    @Test
    public void successfulTicketRetrival() {
        ClientTicket ticket = new ClientTicket(4, "AAAA", "csr");

        Validation<StatusCode, ServerTicket> result = uut.getTicket(ticket);

        assertTrue(result.isSuccess());
        assertEquals(ticket.ticket, result.success().ticket);
        assertEquals(ticket.deviceId, result.success().deviceId);
    }

    @Test
    public void unknownTicketIsReported() {
        ClientTicket ticket = new ClientTicket(3, "unknown", "csr");

        Validation<StatusCode, ServerTicket> result = uut.getTicket(ticket);

        assertTrue(result.isFail());
        assertEquals(StatusCode.NOT_FOUND, result.fail());
    }

    @Test
    public void removeATicket() {
        ClientTicket input = new ClientTicket(3, "AAAA", "csr");
        ServerTicket ticket = uut.getTicket(input).success();

        StatusCode result = uut.removeTicket(ticket);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void removingUnknownTicketIsReported() {
        ServerTicket ticket = new ServerTicket(-1, new Date(), 0, "");

        StatusCode result = uut.removeTicket(ticket);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void gettingUnknownPrincipalsIsReported() {

        Validation<StatusCode, Principals> result = uut.getPrincipalsForTicket("unknown");

        assertTrue(result.isFail());
        assertEquals(StatusCode.NOT_FOUND, result.fail());
    }

    @Test
    public void retrievePrincipalsSuccessfully() {
        Principals expected = new Principals("Alice", "pending_device", 2, 4);

        Validation<StatusCode, Principals> result = uut.getPrincipalsForTicket("AAAA");

        assertTrue(result.isSuccess());
        assertEquals(expected, result.success());
    }

    @Test
    public void addingTicketForDeviceWorks() {
        String ticket = "fdsagrdsbtdsgrafsafea";
        UserDevice device = new UserDevice(4, 0, "pending_device", 2);

        StatusCode result = uut.addTicket(device, ticket);

        Validation<StatusCode, ServerTicket> storedTicket = uut.getTicket(new ClientTicket(device.id, ticket, ""));
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(storedTicket.isSuccess());
        assertEquals(device.id, storedTicket.success().deviceId);
    }
}