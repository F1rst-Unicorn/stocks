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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import de.njsm.stocks.server.v2.business.data.ServerTicket;
import de.njsm.stocks.server.v2.db.TicketHandler;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class TicketAuthoriserTest {

    private TicketAuthoriser uut;

    private AuthAdmin authAdmin;

    private TicketHandler databaseHandler;

    private final int validityTimeInMilliseconds = 10 * 60 * 1000;

    @BeforeEach
    public void setup() {
        authAdmin = Mockito.mock(AuthAdmin.class);
        databaseHandler = Mockito.mock(TicketHandler.class);
        uut = new TicketAuthoriser(authAdmin, databaseHandler, validityTimeInMilliseconds / (60 * 1000));
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(authAdmin);
        verifyNoMoreInteractions(databaseHandler);
    }

    @Test
    public void invalidTicketsDontEraseCertificates() {
        ClientTicket stub = ClientTicket.builder()
                .deviceId(0)
                .ticket("")
                .pemFile("")
                .build();
        Mockito.when(databaseHandler.getTicket(any())).thenReturn(Validation.fail(StatusCode.NOT_FOUND));

        Validation<StatusCode, String> result = uut.handleTicket(stub);

        assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(stub);
        verify(databaseHandler).rollback();
    }

    @Test
    public void thoroughValidationFailsAndWipesCsr() {
        ClientTicket stub = ClientTicket.builder()
                .deviceId(3)
                .ticket("")
                .pemFile("")
                .build();
        ServerTicket storedTicket = ServerTicket.builder()
                .id(0)
                .creationDate(LocalDateTime.now())
                .deviceId(stub.deviceId())
                .ticket("")
                .build();
        Mockito.when(databaseHandler.getTicket(stub)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(any())).thenReturn(Validation.fail(StatusCode.NOT_FOUND));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(stub);

        assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(stub);
        verify(databaseHandler).getPrincipalsForTicket(stub.ticket());
        verify(databaseHandler).rollback();
        verify(authAdmin).saveCsr(stub.deviceId(), stub.pemFile());
        verify(authAdmin).getPrincipals(stub.deviceId());
        verify(authAdmin).wipeDeviceCredentials(stub.deviceId());
    }

    @Test
    public void failingToSaveCsrFailsTheRequest() {
        ClientTicket stub = ClientTicket.builder()
                .deviceId(3)
                .ticket("")
                .pemFile("")
                .build();
        ServerTicket storedTicket = ServerTicket.builder()
                .id(0)
                .creationDate(LocalDateTime.now())
                .deviceId(stub.deviceId())
                .ticket("")
                .build();
        Mockito.when(databaseHandler.getTicket(stub)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(any())).thenReturn(Validation.fail(StatusCode.NOT_FOUND));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.CA_UNREACHABLE);

        Validation<StatusCode, String> result = uut.handleTicket(stub);

        assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(stub);
        verify(databaseHandler).rollback();
        verify(authAdmin).saveCsr(stub.deviceId(), stub.pemFile());
        verify(authAdmin).wipeDeviceCredentials(stub.deviceId());
    }

    @Test
    public void expiredTicketsAreRejected() {
        int deviceId = 3;
        ClientTicket input = ClientTicket.builder()
                .deviceId(deviceId)
                .ticket("")
                .pemFile("")
                .build();
        ServerTicket storedTicket = ServerTicket.builder()
                .id(0)
                .creationDate(LocalDateTime.now().minusNanos(validityTimeInMilliseconds * 1000000L + 1))
                .deviceId(deviceId)
                .ticket("")
                .build();
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));

        Validation<StatusCode, String> result = uut.handleTicket(input);

        assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).rollback();
    }

    @Test
    public void wrongDeviceIdIsRejected() {
        int deviceId = 3;
        ClientTicket input = ClientTicket.builder()
                .deviceId(deviceId)
                .ticket("")
                .pemFile("")
                .build();
        ServerTicket storedTicket = ServerTicket.builder()
                .id(0)
                .creationDate(LocalDateTime.now())
                .deviceId(deviceId-1)
                .ticket("")
                .build();
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));

        Validation<StatusCode, String> result = uut.handleTicket(input);

        assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).rollback();
    }

    @Test
    public void noPrincipalsInDbIsRejected() {
        int deviceId = 3;
        ClientTicket input = ClientTicket.builder()
                .deviceId(deviceId)
                .ticket("")
                .pemFile("")
                .build();
        ServerTicket storedTicket = ServerTicket.builder()
                .id(0)
                .creationDate(LocalDateTime.now())
                .deviceId(deviceId)
                .ticket("")
                .build();
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.ticket())).thenReturn(Validation.fail(StatusCode.NOT_FOUND));
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(Validation.success(new Principals("", "", 1, deviceId)));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(input);

        assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).getPrincipalsForTicket(input.ticket());
        verify(databaseHandler).rollback();
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).wipeDeviceCredentials(deviceId);
    }

    @Test
    public void wrongPrincipalsRejected() {
        int deviceId = 3;
        ClientTicket input = ClientTicket.builder()
                .deviceId(deviceId)
                .ticket("")
                .pemFile("")
                .build();
        ServerTicket storedTicket = ServerTicket.builder()
                .id(0)
                .creationDate(LocalDateTime.now())
                .deviceId(deviceId)
                .ticket("")
                .build();
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.ticket())).thenReturn(
                Validation.success(new Principals("", "", 2, deviceId)));
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(Validation.success(new Principals("", "", 1, deviceId)));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(input);

        assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).getPrincipalsForTicket(input.ticket());
        verify(databaseHandler).rollback();
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).wipeDeviceCredentials(deviceId);
    }

    @Test
    public void invalidCsrIsRejected() {
        int deviceId = 3;
        ClientTicket input = ClientTicket.builder()
                .deviceId(deviceId)
                .ticket("")
                .pemFile("")
                .build();
        ServerTicket storedTicket = ServerTicket.builder()
                .id(0)
                .creationDate(LocalDateTime.now())
                .deviceId(deviceId)
                .ticket("")
                .build();
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.ticket())).thenReturn(
                Validation.success(new Principals("", "", 2, deviceId)));
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(Validation.fail(StatusCode.INVALID_ARGUMENT));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(input);

        assertTrue(result.isFail());
        assertEquals(StatusCode.ACCESS_DENIED, result.fail());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).getPrincipalsForTicket(input.ticket());
        verify(databaseHandler).rollback();
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).wipeDeviceCredentials(deviceId);
    }

    @Test
    public void authorisationProceedsIfTicketIsNotFound() {
        int deviceId = 3;
        Principals p = new Principals("Jack", "Device", 1, deviceId);
        ClientTicket input = ClientTicket.builder()
                .deviceId(deviceId)
                .ticket("")
                .pemFile("")
                .build();
        ServerTicket storedTicket = ServerTicket.builder()
                .id(0)
                .creationDate(LocalDateTime.now())
                .deviceId(deviceId)
                .ticket("")
                .build();
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.ticket())).thenReturn(Validation.success(p));
        Mockito.when(databaseHandler.removeTicket(storedTicket)).thenReturn(StatusCode.NOT_FOUND);
        Mockito.when(databaseHandler.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(Validation.success(p));
        Mockito.when(authAdmin.getCertificate(deviceId)).thenReturn(Validation.success("certificate"));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(input);

        assertTrue(result.isSuccess());
        assertEquals("certificate", result.success());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).getPrincipalsForTicket(input.ticket());
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
        ClientTicket input = ClientTicket.builder()
                .deviceId(deviceId)
                .ticket("")
                .pemFile("")
                .build();
        ServerTicket storedTicket = ServerTicket.builder()
                .id(0)
                .creationDate(LocalDateTime.now())
                .deviceId(deviceId)
                .ticket("")
                .build();
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.ticket())).thenReturn(Validation.success(p));
        Mockito.when(databaseHandler.removeTicket(storedTicket)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(Validation.success(p));
        Mockito.when(authAdmin.getCertificate(deviceId)).thenReturn(Validation.fail(StatusCode.NOT_FOUND));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(input);

        assertTrue(result.isFail());
        assertEquals(StatusCode.NOT_FOUND, result.fail());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).getPrincipalsForTicket(input.ticket());
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
        ClientTicket input = ClientTicket.builder()
                .deviceId(deviceId)
                .ticket("")
                .pemFile("")
                .build();
        ServerTicket storedTicket = ServerTicket.builder()
                .id(0)
                .creationDate(LocalDateTime.now())
                .deviceId(deviceId)
                .ticket("")
                .build();
        Mockito.when(databaseHandler.getTicket(input)).thenReturn(Validation.success(storedTicket));
        Mockito.when(databaseHandler.getPrincipalsForTicket(input.ticket())).thenReturn(Validation.success(p));
        Mockito.when(databaseHandler.removeTicket(storedTicket)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(databaseHandler.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(authAdmin.getPrincipals(deviceId)).thenReturn(Validation.success(p));
        Mockito.when(authAdmin.getCertificate(deviceId)).thenReturn(Validation.success("certificate"));
        Mockito.when(authAdmin.saveCsr(anyInt(), anyString())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.handleTicket(input);

        assertTrue(result.isSuccess());
        assertEquals("certificate", result.success());
        verify(databaseHandler).getTicket(input);
        verify(databaseHandler).getPrincipalsForTicket(input.ticket());
        verify(databaseHandler).removeTicket(storedTicket);
        verify(databaseHandler).commit();
        verify(authAdmin).saveCsr(deviceId, "");
        verify(authAdmin).getPrincipals(deviceId);
        verify(authAdmin).generateCertificate(deviceId);
        verify(authAdmin).getCertificate(deviceId);
    }
}
