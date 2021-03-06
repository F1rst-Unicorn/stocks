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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.ServerTicket;
import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import de.njsm.stocks.server.v2.db.TicketHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class TicketAuthoriserTest {

    private TicketAuthoriser uut;

    private AuthAdmin authAdmin;

    private TicketHandler databaseHandler;

    private final int validityTime = 10 * 60 * 1000;

    @Before
    public void setup() {
        authAdmin = Mockito.mock(AuthAdmin.class);
        databaseHandler = Mockito.mock(TicketHandler.class);
        uut = new TicketAuthoriser(authAdmin, databaseHandler, validityTime / (60 * 1000));
        uut.setPrincipals(TEST_USER);
    }

    @After
    public void tearDown() {
        Mockito.verify(databaseHandler).setPrincipals(TEST_USER);
        verifyNoMoreInteractions(authAdmin);
        verifyNoMoreInteractions(databaseHandler);
    }

    @Test
    public void invalidTicketsDontEraseCertificates() {
        ClientTicket stub = new ClientTicket(0, "", "");
        Mockito.when(databaseHandler.getTicket(any())).thenReturn(Validation.fail(StatusCode.NOT_FOUND));

        Validation<StatusCode, String> result = uut.handleTicket(stub);

        Assert.assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(stub);
        verify(databaseHandler).rollback();
    }

    @Test
    public void thoroughValidationFailsAndWipesCsr() {
        ClientTicket stub = new ClientTicket(3, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), stub.getDeviceId(), "");
        Mockito.when(databaseHandler.getTicket(stub)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(any())).thenReturn(Validation.fail(StatusCode.NOT_FOUND));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(stub);

        Assert.assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(stub);
        verify(databaseHandler).getPrincipalsForTicket(stub.getTicket());
        verify(databaseHandler).rollback();
        verify(authAdmin).saveCsr(stub.getDeviceId(), stub.getPemFile());
        verify(authAdmin).getPrincipals(stub.getDeviceId());
        verify(authAdmin).wipeDeviceCredentials(stub.getDeviceId());
    }

    @Test
    public void failingToSaveCsrFailsTheRequest() {
        ClientTicket stub = new ClientTicket(3, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), stub.getDeviceId(), "");
        Mockito.when(databaseHandler.getTicket(stub)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(any())).thenReturn(Validation.fail(StatusCode.NOT_FOUND));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.CA_UNREACHABLE);

        Validation<StatusCode, String> result = uut.handleTicket(stub);

        Assert.assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(stub);
        verify(databaseHandler).rollback();
        verify(authAdmin).saveCsr(stub.getDeviceId(), stub.getPemFile());
        verify(authAdmin).wipeDeviceCredentials(stub.getDeviceId());
    }

    @Test
    public void expiredTicketsAreRejected() {
        int deviceId = 3;
        ClientTicket input = new ClientTicket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(System.currentTimeMillis() - validityTime - 1), deviceId, "");
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));

        Validation<StatusCode, String> result = uut.handleTicket(input);

        Assert.assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).rollback();
    }

    @Test
    public void wrongDeviceIdIsRejected() {
        int deviceId = 3;
        ClientTicket input = new ClientTicket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), deviceId-1, "");
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));

        Validation<StatusCode, String> result = uut.handleTicket(input);

        Assert.assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).rollback();
    }

    @Test
    public void noPrincipalsInDbIsRejected() {
        int deviceId = 3;
        ClientTicket input = new ClientTicket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), deviceId, "");
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.getTicket())).thenReturn(Validation.fail(StatusCode.NOT_FOUND));
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(Validation.success(new Principals("", "", 1, deviceId)));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(input);

        Assert.assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).getPrincipalsForTicket(input.getTicket());
        verify(databaseHandler).rollback();
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).wipeDeviceCredentials(deviceId);
    }

    @Test
    public void wrongPrincipalsRejected() {
        int deviceId = 3;
        ClientTicket input = new ClientTicket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), deviceId, "");
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.getTicket())).thenReturn(
                Validation.success(new Principals("", "", 2, deviceId)));
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(Validation.success(new Principals("", "", 1, deviceId)));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(input);

        Assert.assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).getPrincipalsForTicket(input.getTicket());
        verify(databaseHandler).rollback();
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).wipeDeviceCredentials(deviceId);
    }

    @Test
    public void invalidCsrIsRejected() {
        int deviceId = 3;
        ClientTicket input = new ClientTicket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), deviceId, "");
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.getTicket())).thenReturn(
                Validation.success(new Principals("", "", 2, deviceId)));
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(Validation.fail(StatusCode.INVALID_ARGUMENT));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(input);

        Assert.assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).getPrincipalsForTicket(input.getTicket());
        verify(databaseHandler).rollback();
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).wipeDeviceCredentials(deviceId);
    }

    @Test
    public void authorisationProceedsIfTicketIsNotFound() {
        int deviceId = 3;
        Principals p = new Principals("Jack", "Device", 1, deviceId);
        ClientTicket input = new ClientTicket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), deviceId, "");
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.getTicket())).thenReturn(Validation.success(p));
        Mockito.when(databaseHandler.removeTicket(storedTicket)).thenReturn(StatusCode.NOT_FOUND);
        Mockito.when(databaseHandler.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(Validation.success(p));
        Mockito.when(authAdmin.getCertificate(deviceId)).thenReturn(Validation.success("certificate"));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(input);

        Assert.assertTrue(result.isSuccess());
        assertEquals("certificate", result.success());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).getPrincipalsForTicket(input.getTicket());
        verify(databaseHandler).removeTicket(storedTicket);
        verify(databaseHandler).commit();
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).generateCertificate(deviceId);
        verify(authAdmin).getCertificate(deviceId);
    }

    @Test
    public void authorisationAbortsIfCertificateIsNotFound() {
        int deviceId = 3;
        Principals p = new Principals("Jack", "Device", 1, deviceId);
        ClientTicket input = new ClientTicket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), deviceId, "");
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.getTicket())).thenReturn(Validation.success(p));
        Mockito.when(databaseHandler.removeTicket(storedTicket)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(Validation.success(p));
        Mockito.when(authAdmin.getCertificate(deviceId)).thenReturn(Validation.fail(StatusCode.NOT_FOUND));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(input);

        Assert.assertTrue(result.isFail());
        assertEquals(StatusCode.NOT_FOUND, result.fail());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).getPrincipalsForTicket(input.getTicket());
        verify(databaseHandler).removeTicket(storedTicket);
        verify(databaseHandler).rollback();
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).generateCertificate(deviceId);
        verify(authAdmin).getCertificate(deviceId);
    }

    @Test
    public void correctTicketIsHandled() {
        int deviceId = 3;
        Principals p = new Principals("Jack", "Device", 1, deviceId);
        ClientTicket input = new ClientTicket(deviceId, "", "");
        ServerTicket storedTicket = new ServerTicket(0, new Date(), deviceId, "");
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.getTicket())).thenReturn(Validation.success(p));
        Mockito.when(databaseHandler.removeTicket(storedTicket)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(databaseHandler.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(Validation.success(p));
        Mockito.when(authAdmin.getCertificate(deviceId)).thenReturn(Validation.success("certificate"));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(input);

        Assert.assertTrue(result.isSuccess());
        assertEquals("certificate", result.success());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).getPrincipalsForTicket(input.getTicket());
        verify(databaseHandler).removeTicket(storedTicket);
        verify(databaseHandler).commit();
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).generateCertificate(deviceId);
        verify(authAdmin).getCertificate(deviceId);
    }
}
