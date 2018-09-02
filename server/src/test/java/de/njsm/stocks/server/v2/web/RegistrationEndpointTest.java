package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.TicketAuthoriser;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import de.njsm.stocks.server.v2.web.data.DataResponse;
import fj.data.Validation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RegistrationEndpointTest {

    private RegistrationEndpoint uut;

    private TicketAuthoriser authoriser;

    @Before
    public void setup() {
        authoriser = Mockito.mock(TicketAuthoriser.class);
        uut = new RegistrationEndpoint(authoriser);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(authoriser);
    }

    @Test
    public void testBusinessObjectCreation() {
        ClientTicket ticket = new ClientTicket(3, "ticket", "csr");
        Mockito.when(authoriser.handleTicket(ticket)).thenReturn(Validation.success("certificate"));

        DataResponse<String> result = uut.getNewCertificate(ticket.deviceId, ticket.ticket, ticket.pemFile);

        Assert.assertEquals(StatusCode.SUCCESS, result.status);
        Assert.assertEquals("certificate", result.data);
        Mockito.verify(authoriser).handleTicket(ticket);
    }
}