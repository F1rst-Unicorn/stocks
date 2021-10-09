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

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.UserDevice;
import de.njsm.stocks.common.api.UserDeviceForDeletion;
import de.njsm.stocks.common.api.UserDeviceForInsertion;
import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.v2.business.data.NewDeviceTicket;
import de.njsm.stocks.server.v2.business.data.UserDeviceForPrincipals;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.TicketHandler;
import de.njsm.stocks.server.v2.db.UserDeviceHandler;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class DeviceManagerTest {

    private UserDeviceHandler dbHandler;

    private FoodItemHandler foodDbHandler;

    private TicketHandler ticketDbHandler;

    private AuthAdmin authAdmin;

    private DeviceManager uut;

    @BeforeEach
    public void setup() {
        dbHandler = Mockito.mock(UserDeviceHandler.class);
        foodDbHandler = Mockito.mock(FoodItemHandler.class);
        ticketDbHandler = Mockito.mock(TicketHandler.class);
        authAdmin = Mockito.mock(AuthAdmin.class);

        uut = new DeviceManager(dbHandler, foodDbHandler, ticketDbHandler, authAdmin);
        uut.setPrincipals(TEST_USER);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verify(dbHandler).setPrincipals(TEST_USER);
        Mockito.verify(foodDbHandler).setPrincipals(TEST_USER);
        Mockito.verify(ticketDbHandler).setPrincipals(TEST_USER);
        Mockito.verifyNoMoreInteractions(dbHandler);
        Mockito.verifyNoMoreInteractions(foodDbHandler);
        Mockito.verifyNoMoreInteractions(ticketDbHandler);
        Mockito.verifyNoMoreInteractions(authAdmin);
    }

    @Test
    public void addDeviceSuccessfully() {
        int newId = 1;
        UserDeviceForInsertion device = UserDeviceForInsertion.builder()
                .name("testdevice")
                .belongsTo(3)
                .build();
        Mockito.when(dbHandler.addReturningId(device)).thenReturn(Validation.success(newId));
        Mockito.when(dbHandler.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(ticketDbHandler.addTicket(eq(newId), any())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, NewDeviceTicket> result = uut.addDevice(device);

        assertTrue(result.isSuccess());
        assertEquals(newId, result.success().deviceId());

        Mockito.verify(dbHandler).addReturningId(device);
        Mockito.verify(dbHandler).commit();
        Mockito.verify(ticketDbHandler).addTicket(eq(newId), any(String.class));
    }

    @Test
    public void addDeviceDbErrorPropagates() {
        UserDeviceForInsertion device = UserDeviceForInsertion.builder()
                .name("testdevice")
                .belongsTo(3)
                .build();
        Mockito.when(dbHandler.addReturningId(device)).thenReturn(Validation.fail(StatusCode.DATABASE_UNREACHABLE));

        Validation<StatusCode, NewDeviceTicket> result = uut.addDevice(device);

        assertTrue(result.isFail());
        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());

        Mockito.verify(dbHandler).addReturningId(device);
        Mockito.verify(dbHandler).rollback();
    }

    @Test
    public void addDeviceTicketErrorPropagates() {
        int newId = 3;
        UserDeviceForInsertion device = UserDeviceForInsertion.builder()
                .name("testdevice")
                .belongsTo(3)
                .build();        Mockito.when(dbHandler.addReturningId(device)).thenReturn(Validation.success(newId));
        Mockito.when(ticketDbHandler.addTicket(eq(newId), any())).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        Validation<StatusCode, NewDeviceTicket> result = uut.addDevice(device);

        assertTrue(result.isFail());
        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());

        Mockito.verify(dbHandler).addReturningId(device);
        Mockito.verify(dbHandler).rollback();
        Mockito.verify(ticketDbHandler).addTicket(eq(newId), any(String.class));
    }

    @Test
    public void gettingDevicesWorks() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(dbHandler.get(false, Instant.EPOCH)).thenReturn(Validation.success(Stream.empty()));
        Mockito.when(dbHandler.commit()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, Stream<UserDevice>> result = uut.get(r, false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        Mockito.verify(dbHandler).get(false, Instant.EPOCH);
        Mockito.verify(dbHandler).setReadOnly();
    }

    @Test
    public void removeDeviceWorks() {
        UserDeviceForDeletion device = UserDeviceForDeletion.builder()
                .id(1)
                .version(2)
                .build();
        Mockito.when(foodDbHandler.transferFoodItems(any(UserDeviceForDeletion.class), any(UserDeviceForPrincipals.class))).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbHandler.delete(device)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbHandler.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(authAdmin.revokeCertificate(device.id())).thenReturn(StatusCode.SUCCESS);
        Mockito.when(ticketDbHandler.removeTicketOfDevice(device)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(device);

        assertEquals(StatusCode.SUCCESS, result);
        ArgumentCaptor<UserDeviceForPrincipals> captor = ArgumentCaptor.forClass(UserDeviceForPrincipals.class);
        Mockito.verify(dbHandler).delete(device);
        Mockito.verify(dbHandler).commit();
        Mockito.verify(ticketDbHandler).removeTicketOfDevice(eq(device));
        Mockito.verify(foodDbHandler).transferFoodItems(eq(device), captor.capture());
        Mockito.verify(authAdmin).revokeCertificate(device.id());
        assertEquals(TEST_USER.toDevice().id(), captor.getValue().id());
    }

    @Test
    public void removeTechnicalDeviceIsRejected() {
        UserDeviceForDeletion device = UserDeviceForDeletion.builder()
                .id(2)
                .version(0)
                .build();
        Mockito.when(dbHandler.isTechnicalUser(device)).thenReturn(Validation.success(true));
        Mockito.when(dbHandler.rollback()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(device);

        assertEquals(StatusCode.ACCESS_DENIED, result);
        Mockito.verify(dbHandler).isTechnicalUser(device);
        Mockito.verify(dbHandler).rollback();
    }

    @Test
    public void revokingDeviceWorks() {
        UserDeviceForDeletion device = UserDeviceForDeletion.builder()
                .id(1)
                .version(2)
                .build();
        Mockito.when(authAdmin.revokeCertificate(device.id())).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.revokeDevice(device);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbHandler).setReadOnly();
        Mockito.verify(dbHandler).commit();
        Mockito.verify(authAdmin).revokeCertificate(device.id());
    }

    @Test
    public void deletingErrorIsPropagated() {
        UserDeviceForDeletion device = UserDeviceForDeletion.builder()
                .id(1)
                .version(2)
                .build();
        Mockito.when(foodDbHandler.transferFoodItems(any(UserDeviceForDeletion.class), any(UserDeviceForPrincipals.class))).thenReturn(StatusCode.SUCCESS);
        Mockito.when(ticketDbHandler.removeTicketOfDevice(device)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbHandler.delete(device)).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.delete(device);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        ArgumentCaptor<UserDeviceForPrincipals> captor = ArgumentCaptor.forClass(UserDeviceForPrincipals.class);
        Mockito.verify(dbHandler).delete(device);
        Mockito.verify(dbHandler).rollback();
        Mockito.verify(foodDbHandler).transferFoodItems(eq(device), captor.capture());
        Mockito.verify(ticketDbHandler).removeTicketOfDevice(eq(device));
        assertEquals(TEST_USER.toDevice().id(), captor.getValue().id());
    }

    @Test
    public void foodItemTransferFailIsPropagated() {
        UserDeviceForDeletion device = UserDeviceForDeletion.builder()
                .id(1)
                .version(2)
                .build();
        Mockito.when(foodDbHandler.transferFoodItems(any(UserDeviceForDeletion.class), any(UserDeviceForPrincipals.class))).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.delete(device);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        ArgumentCaptor<UserDeviceForPrincipals> captor = ArgumentCaptor.forClass(UserDeviceForPrincipals.class);
        Mockito.verify(foodDbHandler).transferFoodItems(eq(device), captor.capture());
        Mockito.verify(dbHandler).rollback();
        assertEquals(TEST_USER.toDevice().id(), captor.getValue().id());
    }
}
