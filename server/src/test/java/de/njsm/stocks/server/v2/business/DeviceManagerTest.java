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

import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.NewDeviceTicket;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.TicketHandler;
import de.njsm.stocks.server.v2.db.UserDeviceHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class DeviceManagerTest {

    private UserDeviceHandler dbHandler;

    private FoodItemHandler foodDbHandler;

    private TicketHandler ticketDbHandler;

    private AuthAdmin authAdmin;

    private DeviceManager uut;

    private UserDevice device;

    @Before
    public void setup() {
        dbHandler = Mockito.mock(UserDeviceHandler.class);
        foodDbHandler = Mockito.mock(FoodItemHandler.class);
        ticketDbHandler = Mockito.mock(TicketHandler.class);
        authAdmin = Mockito.mock(AuthAdmin.class);

        device = new UserDevice(2, 42, "testdevice", 3);

        uut = new DeviceManager(dbHandler, foodDbHandler, ticketDbHandler, authAdmin);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(dbHandler);
        Mockito.verifyNoMoreInteractions(foodDbHandler);
        Mockito.verifyNoMoreInteractions(ticketDbHandler);
        Mockito.verifyNoMoreInteractions(authAdmin);
    }

    @Test
    public void addDeviceSuccessfully() {
        Mockito.when(dbHandler.add(device)).thenReturn(Validation.success(device.id));
        Mockito.when(dbHandler.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(ticketDbHandler.addTicket(eq(device), any())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, NewDeviceTicket> result = uut.addDevice(device);

        assertTrue(result.isSuccess());
        assertEquals(device.id, result.success().deviceId);

        Mockito.verify(dbHandler).add(device);
        Mockito.verify(dbHandler).commit();
        Mockito.verify(ticketDbHandler).addTicket(eq(device), any(String.class));
    }

    @Test
    public void addDeviceDbErrorPropagates() {
        Mockito.when(dbHandler.add(device)).thenReturn(Validation.fail(StatusCode.DATABASE_UNREACHABLE));

        Validation<StatusCode, NewDeviceTicket> result = uut.addDevice(device);

        assertTrue(result.isFail());
        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());

        Mockito.verify(dbHandler).add(device);
        Mockito.verify(dbHandler).rollback();
    }

    @Test
    public void addDeviceTicketErrorPropagates() {
        Mockito.when(dbHandler.add(device)).thenReturn(Validation.success(device.id));
        Mockito.when(ticketDbHandler.addTicket(eq(device), any())).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        Validation<StatusCode, NewDeviceTicket> result = uut.addDevice(device);

        assertTrue(result.isFail());
        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());

        Mockito.verify(dbHandler).add(device);
        Mockito.verify(dbHandler).rollback();
        Mockito.verify(ticketDbHandler).addTicket(eq(device), any(String.class));
    }

    @Test
    public void gettingDevicesWorks() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(dbHandler.get(false)).thenReturn(Validation.success(Stream.empty()));
        Mockito.when(dbHandler.commit()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, Stream<UserDevice>> result = uut.get(r, false);

        assertTrue(result.isSuccess());
        Mockito.verify(dbHandler).get(false);
        Mockito.verify(dbHandler).setReadOnly();
    }

    @Test
    public void gettingDevicesOfUserWorks() {
        User input = new User(1, 2, "Jack");
        Mockito.when(dbHandler.getDevicesOfUser(input)).thenReturn(Validation.success(Collections.emptyList()));

        Validation<StatusCode, List<UserDevice>> result = uut.getDevicesBelonging(input);

        assertTrue(result.isSuccess());
        Mockito.verify(dbHandler).getDevicesOfUser(input);
    }

    @Test
    public void removeDeviceWorks() {
        Mockito.when(foodDbHandler.transferFoodItems(any(UserDevice.class), any(UserDevice.class))).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbHandler.delete(device)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbHandler.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(authAdmin.revokeCertificate(device.id)).thenReturn(StatusCode.SUCCESS);
        Principals currentUser = new Principals("Jack", "Device", 1, 1);

        StatusCode result = uut.removeDevice(device, currentUser);

        assertEquals(StatusCode.SUCCESS, result);
        ArgumentCaptor<UserDevice> captor = ArgumentCaptor.forClass(UserDevice.class);
        Mockito.verify(dbHandler).delete(device);
        Mockito.verify(dbHandler).commit();
        Mockito.verify(foodDbHandler).transferFoodItems(eq(device), captor.capture());
        assertEquals(currentUser.getDid(), captor.getValue().id);
        Mockito.verify(authAdmin).revokeCertificate(device.id);
    }

    @Test
    public void revokingDeviceWorks() {
        Mockito.when(authAdmin.revokeCertificate(device.id)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.revokeDevice(device);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbHandler).setReadOnly();
        Mockito.verify(dbHandler).commit();
        Mockito.verify(authAdmin).revokeCertificate(device.id);
    }

    @Test
    public void deletingErrorIsPropagated() {
        Mockito.when(foodDbHandler.transferFoodItems(any(UserDevice.class), any(UserDevice.class))).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbHandler.delete(device)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        Principals currentUser = new Principals("Jack", "Device", 1, 1);

        StatusCode result = uut.removeDevice(device, currentUser);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        ArgumentCaptor<UserDevice> captor = ArgumentCaptor.forClass(UserDevice.class);
        Mockito.verify(dbHandler).delete(device);
        Mockito.verify(dbHandler).rollback();
        Mockito.verify(foodDbHandler).transferFoodItems(eq(device), captor.capture());
        assertEquals(currentUser.getDid(), captor.getValue().id);
    }

    @Test
    public void foodItemTransferFailIsPropagated() {
        Mockito.when(foodDbHandler.transferFoodItems(any(UserDevice.class), any(UserDevice.class))).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        Principals currentUser = new Principals("Jack", "Device", 1, 1);

        StatusCode result = uut.removeDevice(device, currentUser);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        ArgumentCaptor<UserDevice> captor = ArgumentCaptor.forClass(UserDevice.class);
        Mockito.verify(foodDbHandler).transferFoodItems(eq(device), captor.capture());
        Mockito.verify(dbHandler).rollback();
        assertEquals(currentUser.getDid(), captor.getValue().id);
    }
}
