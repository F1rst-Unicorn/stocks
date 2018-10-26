package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v1.endpoints.BaseTestEndpoint;
import de.njsm.stocks.server.v2.business.DeviceManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.web.data.DataResponse;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

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
        Mockito.when(businessObject.get()).thenReturn(Validation.success(Collections.emptyList()));

        ListResponse<UserDevice> result = uut.getDevices();

        assertEquals(StatusCode.SUCCESS, result.status);
        assertTrue(result.data.isEmpty());
        Mockito.verify(businessObject).get();
    }

    @Test
    public void addDevice() {
        Mockito.when(businessObject.addDevice(any())).thenReturn(Validation.success(new ClientTicket()));
        String name = "test";
        int belongsUser = 2;

        DataResponse<ClientTicket> result = uut.putDevice(name, belongsUser);

        assertEquals(StatusCode.SUCCESS, result.status);
        ArgumentCaptor<UserDevice> captor = ArgumentCaptor.forClass(UserDevice.class);
        Mockito.verify(businessObject).addDevice(captor.capture());
        assertEquals(name, captor.getValue().name);
        assertEquals(belongsUser, captor.getValue().userId);
    }

    @Test
    public void addDeviceInvalidNameIsRejected() {
        String name = "";
        int belongsUser = 2;

        DataResponse<ClientTicket> result = uut.putDevice(name, belongsUser);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void addDeviceInvalidUserIsRejected() {
        String name = "test";
        int belongsUser = 0;

        DataResponse<ClientTicket> result = uut.putDevice(name, belongsUser);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deleteDevice() {
        Mockito.when(businessObject.removeDevice(any(), eq(PrincipalFilterTest.TEST_USER))).thenReturn(StatusCode.SUCCESS);
        int id = 4;
        int version = 3;

        Response result = uut.deleteDevice(BaseTestEndpoint.createMockRequest(),
                id, version);

        assertEquals(StatusCode.SUCCESS, result.status);
        ArgumentCaptor<UserDevice> captor = ArgumentCaptor.forClass(UserDevice.class);
        Mockito.verify(businessObject).removeDevice(captor.capture(), eq(PrincipalFilterTest.TEST_USER));
        assertEquals(id, captor.getValue().id);
        assertEquals(version, captor.getValue().version);
    }

    @Test
    public void deleteInvalidIdIsRejected() {
        int id = 0;
        int version = 3;

        Response result = uut.deleteDevice(BaseTestEndpoint.createMockRequest(),
                id, version);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deleteInvalidVersionIsRejected() {
        int id = 3;
        int version = -1;

        Response result = uut.deleteDevice(BaseTestEndpoint.createMockRequest(),
                id, version);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void revokeDevice() {
        Mockito.when(businessObject.revokeDevice(any())).thenReturn(StatusCode.SUCCESS);
        int id = 4;
        int version = 3;

        Response result = uut.revokeDevice(BaseTestEndpoint.createMockRequest(),
                id, version);

        assertEquals(StatusCode.SUCCESS, result.status);
        ArgumentCaptor<UserDevice> captor = ArgumentCaptor.forClass(UserDevice.class);
        Mockito.verify(businessObject).revokeDevice(captor.capture());
        assertEquals(id, captor.getValue().id);
        assertEquals(version, captor.getValue().version);
    }


    @Test
    public void revokeInvalidIdIsRejected() {
        int id = 0;
        int version = 3;

        Response result = uut.revokeDevice(BaseTestEndpoint.createMockRequest(),
                id, version);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void revokeInvalidVersionIsRejected() {
        int id = 3;
        int version = -1;

        Response result = uut.revokeDevice(BaseTestEndpoint.createMockRequest(),
                id, version);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

}