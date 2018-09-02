package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.business.UserContextFactory;
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

    private UserContextFactory authAdmin;

    private TicketAuthoriser authoriser;

    private RegistrationEndpoint uut;

    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        authAdmin = Mockito.mock(UserContextFactory.class);
        authoriser = Mockito.mock(TicketAuthoriser.class);
        uut = new RegistrationEndpoint(handler, authAdmin, authoriser);
    }

    @Test
    public void testBusinessObjectCreation() {
        ClientTicket ticket = new ClientTicket(2, "ticket", "csr");
        Mockito.when(authoriser.handleTicket(ticket)).thenReturn(Validation.success("certificate"));

        ClientTicket result = uut.getNewCertificate(createMockRequest(), ticket);

        Assert.assertEquals("certificate", result.pemFile);
        Mockito.verify(authoriser).handleTicket(ticket);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(authAdmin);
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
        Mockito.verifyNoMoreInteractions(authAdmin);
        Mockito.verifyNoMoreInteractions(authoriser);
    }
}
