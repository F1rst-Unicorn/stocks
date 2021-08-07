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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.common.api.UserDeviceForDeletion;
import de.njsm.stocks.common.api.UserDeviceForInsertion;
import de.njsm.stocks.server.v2.business.DeviceManager;
import de.njsm.stocks.server.v2.business.data.NewDeviceTicket;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.verify;

public class DeviceEndpointTest {

    private DeviceManager businessObject;

    private DeviceEndpoint uut;

    @Before
    public void setup() {
        businessObject = Mockito.mock(DeviceManager.class);

        uut = new DeviceEndpoint(businessObject);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(businessObject);
    }

    @Test
    public void getDevices() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(businessObject.get(r, false, Instant.EPOCH)).thenReturn(Validation.success(Stream.empty()));

        uut.get(r, 0, null);

        ArgumentCaptor<StreamResponse<UserDevice>> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(StatusCode.SUCCESS, c.getValue().getStatus());
        assertEquals(0, c.getValue().data.count());
        Mockito.verify(businessObject).get(r, false, Instant.EPOCH);
    }

    @Test
    public void getDevicesFromInvalidStartingPoint() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);

        uut.get(r, 1, "invalid");

        ArgumentCaptor<Response> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(StatusCode.INVALID_ARGUMENT, c.getValue().getStatus());
    }

    @Test
    public void addDevice() {
        UserDeviceForInsertion device = new UserDeviceForInsertion("test", 2);
        Mockito.when(businessObject.addDevice(device)).thenReturn(Validation.success(new NewDeviceTicket(0, "")));

        DataResponse<NewDeviceTicket> result = uut.putDevice(createMockRequest(), device.getName(), device.getBelongsTo());

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(businessObject).addDevice(device);
        Mockito.verify(businessObject).setPrincipals(TEST_USER);
    }

    @Test
    public void addDeviceInvalidNameIsRejected() {
        String name = "";
        int belongsUser = 2;

        DataResponse<NewDeviceTicket> result = uut.putDevice(createMockRequest(), name, belongsUser);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void addDeviceContainingDollarIsRejected() {
        String name = "John$1";
        int belongsUser = 2;

        DataResponse<NewDeviceTicket> result = uut.putDevice(createMockRequest(), name, belongsUser);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void addDeviceContainingEqualSignIsRejected() {
        String name = "John=1";
        int belongsUser = 2;

        DataResponse<NewDeviceTicket> result = uut.putDevice(createMockRequest(), name, belongsUser);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void addDeviceInvalidUserIsRejected() {
        String name = "test";
        int belongsUser = 0;

        DataResponse<NewDeviceTicket> result = uut.putDevice(createMockRequest(), name, belongsUser);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deleteDevice() {
        UserDeviceForDeletion device = new UserDeviceForDeletion(4, 3);
        Mockito.when(businessObject.delete(device)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.deleteDevice(createMockRequest(),
                device.id(),
                device.version());

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(businessObject).delete(device);
        Mockito.verify(businessObject).setPrincipals(TEST_USER);
    }

    @Test
    public void deleteInvalidIdIsRejected() {
        int id = 0;
        int version = 3;

        Response result = uut.deleteDevice(Util.createMockRequest(),
                id, version);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deleteInvalidVersionIsRejected() {
        int id = 3;
        int version = -1;

        Response result = uut.deleteDevice(Util.createMockRequest(),
                id, version);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void revokeDevice() {
        UserDeviceForDeletion device = new UserDeviceForDeletion(4, 3);
        Mockito.when(businessObject.revokeDevice(device)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.revokeDevice(Util.createMockRequest(),
                device.id(),
                device.version());

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(businessObject).revokeDevice(device);
        Mockito.verify(businessObject).setPrincipals(TEST_USER);
    }


    @Test
    public void revokeInvalidIdIsRejected() {
        int id = 0;
        int version = 3;

        Response result = uut.revokeDevice(Util.createMockRequest(),
                id, version);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void revokeInvalidVersionIsRejected() {
        int id = 3;
        int version = -1;

        Response result = uut.revokeDevice(Util.createMockRequest(),
                id, version);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

}
