package de.njsm.stocks.server.v1.internal.business;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.common.data.ServerTicket;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class TicketAuthoriserTest {

    private TicketAuthoriser uut;

    private AuthAdmin authAdmin;

    private DatabaseHandler databaseHandler;

    private int validityTime = 10 * 60 * 1000;

    @Before
    public void setup() throws Exception {
        authAdmin = Mockito.mock(AuthAdmin.class);
        databaseHandler = Mockito.mock(DatabaseHandler.class);
        uut = new TicketAuthoriser(authAdmin, databaseHandler, validityTime / (60 * 1000));
    }

    @Test
    public void invalidTicketsDontEraseCertificates() {
        Ticket stub = new Ticket();

        Ticket result = uut.handleTicket(stub);

        assertNull(result.pemFile);
        verify(databaseHandler).getTicket(stub.ticket);
        verifyNoMoreInteractions(authAdmin);
        verifyNoMoreInteractions(databaseHandler);
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
        verifyNoMoreInteractions(databaseHandler);
    }

    @Test
    public void expiredTicketsAreRejected() {
        int deviceId = 3;
        Ticket input = new Ticket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(System.currentTimeMillis() - validityTime), deviceId, "");
        Mockito.when(databaseHandler.getTicket(input.ticket)).thenReturn(storedTicket);

        Ticket result = uut.handleTicket(input);

        assertNull(result.pemFile);
        verify(databaseHandler).getTicket(input.ticket);
        verifyNoMoreInteractions(authAdmin);
        verifyNoMoreInteractions(databaseHandler);
    }

    @Test
    public void wrongDeviceIdIsRejected() {
        int deviceId = 3;
        Ticket input = new Ticket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), deviceId-1, "");
        Mockito.when(databaseHandler.getTicket(input.ticket)).thenReturn(storedTicket);

        Ticket result = uut.handleTicket(input);

        assertNull(result.pemFile);
        verify(databaseHandler).getTicket(input.ticket);
        verifyNoMoreInteractions(authAdmin);
        verifyNoMoreInteractions(databaseHandler);
    }

    @Test
    public void noPrincipalsInDbIsRejected() throws IOException {
        int deviceId = 3;
        Ticket input = new Ticket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), deviceId, "");
        Mockito.when(databaseHandler.getTicket(input.ticket)).thenReturn(storedTicket);
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(new Principals("", "", 1, deviceId));

        Ticket result = uut.handleTicket(input);

        assertNull(result.pemFile);
        verify(databaseHandler).getTicket(input.ticket);
        verify(databaseHandler).getPrincipalsForTicket(input.ticket);
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).wipeDeviceCredentials(deviceId);
        verifyNoMoreInteractions(authAdmin);
        verifyNoMoreInteractions(databaseHandler);
    }

    @Test
    public void wrongPrinciplesRejected() throws IOException {
        int deviceId = 3;
        Ticket input = new Ticket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), deviceId, "");
        Mockito.when(databaseHandler.getTicket(input.ticket)).thenReturn(storedTicket);
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.ticket)).thenReturn(
                new Principals("", "", 2, deviceId));
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(new Principals("", "", 1, deviceId));

        Ticket result = uut.handleTicket(input);

        assertNull(result.pemFile);
        verify(databaseHandler).getTicket(input.ticket);
        verify(databaseHandler).getPrincipalsForTicket(input.ticket);
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).wipeDeviceCredentials(deviceId);
        verifyNoMoreInteractions(authAdmin);
        verifyNoMoreInteractions(databaseHandler);
    }

    @Test
    public void correctTicketIsHandled() throws IOException {
        int deviceId = 3;
        Principals p = new Principals("Jack", "Device", 1, deviceId);
        Ticket input = new Ticket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), deviceId, "");
        Mockito.when(databaseHandler.getTicket(input.ticket)).thenReturn(storedTicket);
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.ticket)).thenReturn(p);
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(p);

        Ticket result = uut.handleTicket(input);

        assertNull(result.pemFile);
        verify(databaseHandler).getTicket(input.ticket);
        verify(databaseHandler).getPrincipalsForTicket(input.ticket);
        verify(databaseHandler).remove(storedTicket);
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).generateCertificate(deviceId);
        verify(authAdmin).getCertificate(deviceId);
        verifyNoMoreInteractions(authAdmin);
        verifyNoMoreInteractions(databaseHandler);
    }
}