package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.LocationManager;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static de.njsm.stocks.server.v2.business.StatusCode.INVALID_ARGUMENT;
import static de.njsm.stocks.server.v2.business.StatusCode.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocationEndpointTest {

    private LocationEndpoint uut;

    private LocationManager businessLayer;

    @Before
    public void setup() {
        businessLayer = Mockito.mock(LocationManager.class);
        uut = new LocationEndpoint(businessLayer);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(businessLayer);
    }

    @Test
    public void puttingNullCodeIsInvalid() {

        Response result = uut.putLocation(null);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void puttingEmptyCodeIsInvalid() {

        Response result = uut.putLocation("");

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void renamingInvalidIdIsInvalid() {

        Response result = uut.renameLocation(0, 1, "fdsa");

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void renamingInvalidVersionIsInvalid() {

        Response result = uut.renameLocation(1, -1, "fdsa");

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void renamingToInvalidNameIsInvalid() {

        Response result = uut.renameLocation(1, 1, "");

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deletingInvalidIdIsInvalid() {

        Response result = uut.deleteLocation(0, 1, 0);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deletingInvalidVersionIsInvalid() {

        Response result = uut.deleteLocation(1, -1, 0);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void foodIsAdded() {
        Location data = new Location(0, "Banana", 0);
        when(businessLayer.put(data)).thenReturn(SUCCESS);

        Response response = uut.putLocation(data.name);

        assertEquals(SUCCESS, response.status);
        verify(businessLayer).put(data);
    }

    @Test
    public void getLocationReturnsList() {
        List<Location> data = Collections.singletonList(new Location(2, "Banana", 2));
        when(businessLayer.get()).thenReturn(Validation.success(data));

        ListResponse<Location> response = uut.getLocation();

        assertEquals(SUCCESS, response.status);
        assertEquals(data, response.data);
        verify(businessLayer).get();
    }

    @Test
    public void renameLocationWorks() {
        Location data = new Location(1, "", 2);
        String newName = "Bread";
        when(businessLayer.rename(data, newName)).thenReturn(SUCCESS);

        Response response = uut.renameLocation(data.id, data.version, newName);

        assertEquals(SUCCESS, response.status);
        verify(businessLayer).rename(data, newName);
    }

    @Test
    public void deleteLocationWorks() {
        Location data = new Location(1, "", 2);
        when(businessLayer.delete(data, false)).thenReturn(SUCCESS);

        Response response = uut.deleteLocation(data.id, data.version, 0);

        assertEquals(SUCCESS, response.status);
        verify(businessLayer).delete(data, false);
    }

    @Test
    public void deleteLocationWorksCascading() {
        Location data = new Location(1, "", 2);
        when(businessLayer.delete(data, true)).thenReturn(SUCCESS);

        Response response = uut.deleteLocation(data.id, data.version, 1);

        assertEquals(SUCCESS, response.status);
        verify(businessLayer).delete(data, true);
    }
}