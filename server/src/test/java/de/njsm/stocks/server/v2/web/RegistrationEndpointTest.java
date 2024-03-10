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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.DataResponse;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.business.TicketAuthoriser;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegistrationEndpointTest {

    private RegistrationEndpoint uut;

    private TicketAuthoriser authoriser;

    @BeforeEach
    public void setup() {
        authoriser = Mockito.mock(TicketAuthoriser.class);
        uut = new RegistrationEndpoint(authoriser);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(authoriser);
    }

    @Test
    public void emptyTokenIsInvalid() {

        DataResponse<String> result = uut.getNewCertificate(1, "", "csr");

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void emptyCsrIsInvalid() {

        DataResponse<String> result = uut.getNewCertificate(1, "token", "");

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void invalidIdIsInvalid() {

        DataResponse<String> result = uut.getNewCertificate(0, "token", "csr");

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void testBusinessObjectCreation() {
        ClientTicket ticket = ClientTicket.builder()
                .deviceId(3)
                .ticket("ticket")
                .pemFile("csr")
                .build();
        Mockito.when(authoriser.handleTicket(ticket)).thenReturn(Validation.success("certificate"));

        DataResponse<String> result = uut.getNewCertificate(ticket.deviceId(), ticket.ticket(), ticket.pemFile());

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        assertEquals("certificate", result.data);
        Mockito.verify(authoriser).handleTicket(ticket);
    }
}
