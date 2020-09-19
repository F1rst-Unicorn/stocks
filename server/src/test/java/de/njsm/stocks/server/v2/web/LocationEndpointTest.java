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

import de.njsm.stocks.server.v2.business.LocationManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.web.data.Response;
import de.njsm.stocks.server.v2.web.data.StreamResponse;
import fj.data.Validation;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.server.v2.business.StatusCode.INVALID_ARGUMENT;
import static de.njsm.stocks.server.v2.business.StatusCode.SUCCESS;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
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

        Response result = uut.putLocation(createMockRequest(), null);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void puttingEmptyCodeIsInvalid() {

        Response result = uut.putLocation(createMockRequest(), "");

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void renamingInvalidIdIsInvalid() {

        Response result = uut.renameLocation(createMockRequest(), 0, 1, "fdsa");

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void renamingInvalidVersionIsInvalid() {

        Response result = uut.renameLocation(createMockRequest(), 1, -1, "fdsa");

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void renamingToInvalidNameIsInvalid() {

        Response result = uut.renameLocation(createMockRequest(), 1, 1, "");

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deletingInvalidIdIsInvalid() {

        Response result = uut.deleteLocation(createMockRequest(), 0, 1, 0);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deletingInvalidVersionIsInvalid() {

        Response result = uut.deleteLocation(createMockRequest(), 1, -1, 0);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void foodIsAdded() {
        Location data = new Location(0, "Banana", 0);
        when(businessLayer.put(data)).thenReturn(SUCCESS);

        Response response = uut.putLocation(createMockRequest(), data.name);

        assertEquals(SUCCESS, response.status);
        verify(businessLayer).put(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void getLocationReturnsList() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        List<Location> data = Collections.singletonList(new Location(2, "Banana", 2));
        when(businessLayer.get(r, false, Instant.EPOCH)).thenReturn(Validation.success(data.stream()));

        uut.get(r, 0, null);

        ArgumentCaptor<StreamResponse<Location>> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(SUCCESS, c.getValue().status);
        assertEquals(data, c.getValue().data.collect(Collectors.toList()));
        verify(businessLayer).get(r, false, Instant.EPOCH);
    }

    @Test
    public void getLocationsFromInvalidStartingPoint() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);

        uut.get(r, 1, "invalid");

        ArgumentCaptor<Response> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        TestCase.assertEquals(StatusCode.INVALID_ARGUMENT, c.getValue().status);
    }

    @Test
    public void renameLocationWorks() {
        String newName = "Bread";
        Location data = new Location(1, newName, 2);
        when(businessLayer.rename(data)).thenReturn(SUCCESS);

        Response response = uut.renameLocation(createMockRequest(), data.id, data.version, newName);

        assertEquals(SUCCESS, response.status);
        verify(businessLayer).rename(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void deleteLocationWorks() {
        Location data = new Location(1, "", 2);
        when(businessLayer.delete(data, false)).thenReturn(SUCCESS);

        Response response = uut.deleteLocation(createMockRequest(), data.id, data.version, 0);

        assertEquals(SUCCESS, response.status);
        verify(businessLayer).delete(data, false);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void deleteLocationWorksCascading() {
        Location data = new Location(1, "", 2);
        when(businessLayer.delete(data, true)).thenReturn(SUCCESS);

        Response response = uut.deleteLocation(createMockRequest(), data.id, data.version, 1);

        assertEquals(SUCCESS, response.status);
        verify(businessLayer).delete(data, true);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }
}
