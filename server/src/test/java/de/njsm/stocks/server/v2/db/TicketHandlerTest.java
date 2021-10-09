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

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.UserDeviceForDeletion;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import de.njsm.stocks.server.v2.business.data.ServerTicket;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static de.njsm.stocks.server.v2.db.jooq.tables.Ticket.TICKET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TicketHandlerTest extends DbTestCase {

    private TicketHandler uut;

    @BeforeEach
    public void setup() {
        uut = new TicketHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
    }

    @Test
    public void successfulTicketRetrival() {
        ClientTicket ticket = ClientTicket.builder()
                .deviceId(6)
                .ticket("AAAA")
                .pemFile("csr")
                .build();

        Validation<StatusCode, ServerTicket> result = uut.getTicket(ticket);

        assertTrue(result.isSuccess());
        assertEquals(ticket.ticket(), result.success().ticket());
        assertEquals(ticket.deviceId(), result.success().deviceId());
    }

    @Test
    public void unknownTicketIsReported() {
        ClientTicket ticket = ClientTicket.builder()
                .deviceId(3)
                .ticket("unknown")
                .pemFile("csr")
                .build();

        Validation<StatusCode, ServerTicket> result = uut.getTicket(ticket);

        assertTrue(result.isFail());
        assertEquals(StatusCode.NOT_FOUND, result.fail());
    }

    @Test
    public void removeATicket() {
        ClientTicket input = ClientTicket.builder()
                .deviceId(3)
                .ticket("AAAA")
                .pemFile("csr")
                .build();
        ServerTicket ticket = uut.getTicket(input).success();

        StatusCode result = uut.removeTicket(ticket);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void removingUnknownTicketIsReported() {
        ServerTicket ticket = ServerTicket.builder()
                .id(-1)
                .creationDate(new Date())
                .deviceId(0)
                .ticket("")
                .build();

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
        Principals expected = new Principals("Alice", "pending_device", 4, 6);

        Validation<StatusCode, Principals> result = uut.getPrincipalsForTicket("AAAA");

        assertTrue(result.isSuccess());
        assertEquals(expected, result.success());
    }

    @Test
    public void addingTicketForDeviceWorks() {
        String ticket = "fdsagrdsbtdsgrafsafea";
        int id = 4;

        StatusCode result = uut.addTicket(id, ticket);

        Validation<StatusCode, ServerTicket> storedTicket = uut.getTicket(ClientTicket.builder()
                .deviceId(id)
                .ticket(ticket)
                .pemFile("")
                .build());
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(storedTicket.isSuccess());
        assertEquals(id, storedTicket.success().deviceId());
    }

    @Test
    public void removingTicketOfDeviceWorks() {
        UserDeviceForDeletion device = UserDeviceForDeletion.builder()
                .id(6)
                .version(0)
                .build();
        long numberOfTickets = getDSLContext().selectFrom(TICKET).stream().count();
        assertEquals(1, numberOfTickets);

        StatusCode result = uut.removeTicketOfDevice(device);

        assertEquals(StatusCode.SUCCESS, result);
        numberOfTickets = getDSLContext().selectFrom(TICKET).stream().count();
        assertEquals(0, numberOfTickets);
    }
}
