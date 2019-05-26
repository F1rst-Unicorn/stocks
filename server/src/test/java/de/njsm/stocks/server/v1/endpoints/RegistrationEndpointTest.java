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

package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.TicketAuthoriser;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import fj.data.Validation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RegistrationEndpointTest extends BaseTestEndpoint {

    private DatabaseHandler handler;

    private TicketAuthoriser authoriser;

    private RegistrationEndpoint uut;

    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        authoriser = Mockito.mock(TicketAuthoriser.class);
        uut = new RegistrationEndpoint(handler, authoriser);
    }

    @Test
    public void testBusinessObjectCreation() {
        ClientTicket ticket = new ClientTicket(2, "ticket", "csr");
        Mockito.when(authoriser.handleTicket(ticket)).thenReturn(Validation.success("certificate"));

        ClientTicket result = uut.getNewCertificate(createMockRequest(), ticket);

        Assert.assertEquals("certificate", result.pemFile);
        Mockito.verify(authoriser).handleTicket(ticket);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(authoriser);
    }

    @Test
    public void errorsPropagateFromBusiness() {
        ClientTicket ticket = new ClientTicket(2, "ticket", "csr");
        Mockito.when(authoriser.handleTicket(ticket)).thenReturn(Validation.fail(StatusCode.ACCESS_DENIED));

        ClientTicket result = uut.getNewCertificate(createMockRequest(), ticket);

        Assert.assertNull(result.pemFile);
        Mockito.verify(authoriser).handleTicket(ticket);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(authoriser);
    }
}
