package de.njsm.stocks.server.internal.business;

import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.server.internal.auth.AuthAdmin;
import de.njsm.stocks.server.internal.db.DatabaseHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class TicketAuthoriserTest {

    private TicketAuthoriser uut;

    private AuthAdmin authAdmin;

    private DatabaseHandler databaseHandler;

    @Before
    public void setup() throws Exception {
        authAdmin = Mockito.mock(AuthAdmin.class);
        databaseHandler = Mockito.mock(DatabaseHandler.class);
        uut = new TicketAuthoriser(authAdmin, databaseHandler, 10);
    }

    @Test
    public void invalidTicketsDontEraseCertificates() {
        Ticket stub = new Ticket();

        Ticket result = uut.handleTicket(stub);

        assertNull(result.pemFile);
        verify(databaseHandler).getTicket(stub.ticket);
        verifyNoMoreInteractions(authAdmin);
    }

    @Test
    public void laterExceptionsEraseCertificate() {
        Mockito.when(databaseHandler.getTicket(any())).thenThrow(new SecurityException("Mockito"));
        Ticket stub = new Ticket(3, "", "");

        Ticket result = uut.handleTicket(stub);

        assertNull(result.pemFile);
        verify(databaseHandler).getTicket(stub.ticket);
        verify(authAdmin).wipeDeviceCredentials(stub.deviceId);
        verifyNoMoreInteractions(authAdmin);
    }
}