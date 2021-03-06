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
import de.njsm.stocks.server.v2.business.data.*;
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

import static de.njsm.stocks.server.v2.business.StatusCode.*;
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

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void puttingEmptyCodeIsInvalid() {

        Response result = uut.putLocation(createMockRequest(), "");

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void renamingInvalidIdIsInvalid() {

        Response result = uut.renameLocation(createMockRequest(), 0, 1, "fdsa");

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void renamingInvalidVersionIsInvalid() {

        Response result = uut.renameLocation(createMockRequest(), 1, -1, "fdsa");

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void renamingToInvalidNameIsInvalid() {

        Response result = uut.renameLocation(createMockRequest(), 1, 1, "");

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deletingInvalidIdIsInvalid() {

        Response result = uut.deleteLocation(createMockRequest(), 0, 1, 0);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deletingInvalidVersionIsInvalid() {

        Response result = uut.deleteLocation(createMockRequest(), 1, -1, 0);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void locationIsAdded() {
        LocationForInsertion data = new LocationForInsertion("Banana");
        when(businessLayer.put(data)).thenReturn(SUCCESS);

        Response response = uut.putLocation(createMockRequest(), data.getName());

        assertEquals(SUCCESS, response.getStatus());
        verify(businessLayer).put(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void getLocationReturnsList() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        List<Location> data = Collections.singletonList(new LocationForGetting(2, 3, "Banana", ""));
        when(businessLayer.get(r, false, Instant.EPOCH)).thenReturn(Validation.success(data.stream()));

        uut.get(r, 0, null);

        ArgumentCaptor<StreamResponse<Location>> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(SUCCESS, c.getValue().getStatus());
        assertEquals(data, c.getValue().data.collect(Collectors.toList()));
        verify(businessLayer).get(r, false, Instant.EPOCH);
    }

    @Test
    public void getLocationsFromInvalidStartingPoint() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);

        uut.get(r, 1, "invalid");

        ArgumentCaptor<Response> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        TestCase.assertEquals(INVALID_ARGUMENT, c.getValue().getStatus());
    }

    @Test
    public void renameLocationWorks() {
        LocationForRenaming data = new LocationForRenaming(1, 3, "Bread");
        when(businessLayer.rename(data)).thenReturn(SUCCESS);

        Response response = uut.renameLocation(createMockRequest(), data.getId(), data.getVersion(), data.getNewName());

        assertEquals(SUCCESS, response.getStatus());
        verify(businessLayer).rename(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void deleteLocationWorks() {
        LocationForDeletion data = new LocationForDeletion(1, 2);
        when(businessLayer.delete(data, false)).thenReturn(SUCCESS);

        Response response = uut.deleteLocation(createMockRequest(), data.getId(), data.getVersion(), 0);

        assertEquals(SUCCESS, response.getStatus());
        verify(businessLayer).delete(data, false);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void deleteLocationWorksCascading() {
        LocationForDeletion data = new LocationForDeletion(1, 2);
        when(businessLayer.delete(data, true)).thenReturn(SUCCESS);

        Response response = uut.deleteLocation(createMockRequest(), data.getId(), data.getVersion(), 1);

        assertEquals(SUCCESS, response.getStatus());
        verify(businessLayer).delete(data, true);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void settingDescriptionIsForwarded() {
        LocationForSetDescription data = new LocationForSetDescription(1, 2, "new description");
        when(businessLayer.setDescription(data)).thenReturn(SUCCESS);

        Response response = uut.setDescription(createMockRequest(), data.getId(), data.getVersion(), data.getDescription());

        assertEquals(SUCCESS, response.getStatus());
        verify(businessLayer).setDescription(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void emptyDescriptionIsForwarded() {
        LocationForSetDescription data = new LocationForSetDescription(1, 2, "");
        when(businessLayer.setDescription(data)).thenReturn(SUCCESS);

        Response response = uut.setDescription(createMockRequest(), data.getId(), data.getVersion(), data.getDescription());

        assertEquals(SUCCESS, response.getStatus());
        verify(businessLayer).setDescription(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void errorFromBackendIsPropagated() {
        LocationForSetDescription data = new LocationForSetDescription(1, 2, "new description");
        when(businessLayer.setDescription(data)).thenReturn(INVALID_DATA_VERSION);

        Response response = uut.setDescription(createMockRequest(), data.getId(), data.getVersion(), data.getDescription());

        assertEquals(INVALID_DATA_VERSION, response.getStatus());
        verify(businessLayer).setDescription(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void settingDescriptionWithInvalidIdIsRejected() {
        Response response = uut.setDescription(createMockRequest(), 0, 2, "fdsa");
        assertEquals(INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void settingDescriptionWithInvalidVersionIsRejected() {
        Response response = uut.setDescription(createMockRequest(), 1, -2, "fdsa");
        assertEquals(INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void settingDescriptionWithInvalidDescriptionIsRejected() {
        Response response = uut.setDescription(createMockRequest(), 1, 2, null);
        assertEquals(INVALID_ARGUMENT, response.getStatus());
    }
}
