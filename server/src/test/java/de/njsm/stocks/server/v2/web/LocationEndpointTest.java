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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.common.api.serialisers.InstantSerialiser;
import de.njsm.stocks.server.v2.business.LocationManager;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.common.api.StatusCode.*;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocationEndpointTest {

    private LocationEndpoint uut;

    private LocationManager businessLayer;

    @BeforeEach
    public void setup() {
        businessLayer = Mockito.mock(LocationManager.class);
        uut = new LocationEndpoint(businessLayer);
    }

    @AfterEach
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
        LocationForInsertion data = LocationForInsertion.builder()
                .name("Banana")
                .build();
        when(businessLayer.put(data)).thenReturn(SUCCESS);

        Response response = uut.putLocation(createMockRequest(), data.name());

        assertEquals(SUCCESS, response.getStatus());
        verify(businessLayer).put(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void getLocationReturnsList() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        BitemporalLocation item = BitemporalLocation.builder()
                .id(2)
                .version(3)
                .validTimeStart(Instant.EPOCH)
                .validTimeEnd(Instant.EPOCH)
                .transactionTimeStart(Instant.EPOCH)
                .transactionTimeEnd(Instant.EPOCH)
                .initiates(3)
                .name("Banana")
                .description("")
                .build();
        List<Location> data = Collections.singletonList(item);
        when(businessLayer.get(r, Instant.EPOCH, Instant.EPOCH)).thenReturn(Validation.success(data.stream()));

        uut.get(r, InstantSerialiser.serialize(Instant.EPOCH), InstantSerialiser.serialize(Instant.EPOCH));

        ArgumentCaptor<StreamResponse<Location>> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(SUCCESS, c.getValue().getStatus());
        assertEquals(data, c.getValue().data.collect(Collectors.toList()));
        verify(businessLayer).get(r, Instant.EPOCH, Instant.EPOCH);
    }

    @Test
    public void getLocationsFromInvalidStartingPoint() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);

        uut.get(r, "invalid", "invalid");

        ArgumentCaptor<Response> c = ArgumentCaptor.forClass(Response.class);
        verify(r).resume(c.capture());
        assertEquals(INVALID_ARGUMENT, c.getValue().getStatus());
    }

    @Test
    public void renameLocationWorks() {
        LocationForRenaming data = LocationForRenaming.builder()
                .id(1)
                .version(3)
                .name("Bread")
                .build();
        when(businessLayer.rename(data)).thenReturn(SUCCESS);

        Response response = uut.renameLocation(createMockRequest(), data.id(), data.version(), data.name());

        assertEquals(SUCCESS, response.getStatus());
        verify(businessLayer).rename(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void deleteLocationWorks() {
        LocationForDeletion data = LocationForDeletion.builder()
                .id(1)
                .version(2)
                .cascade(false)
                .build();
        when(businessLayer.delete(data)).thenReturn(SUCCESS);

        Response response = uut.deleteLocation(createMockRequest(), data.id(), data.version(), 0);

        assertEquals(SUCCESS, response.getStatus());
        verify(businessLayer).delete(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void deleteLocationWorksCascading() {
        LocationForDeletion data = LocationForDeletion.builder()
                .id(1)
                .version(2)
                .cascade(true)
                .build();
        when(businessLayer.delete(data)).thenReturn(SUCCESS);

        Response response = uut.deleteLocation(createMockRequest(), data.id(), data.version(), 1);

        assertEquals(SUCCESS, response.getStatus());
        verify(businessLayer).delete(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void settingDescriptionIsForwarded() {
        LocationForSetDescription data = LocationForSetDescription.builder()
                .id(1)
                .version(2)
                .description("new description")
                .build();
        when(businessLayer.setDescription(data)).thenReturn(SUCCESS);

        Response response = uut.setDescription(createMockRequest(), data.id(), data.version(), data.description());

        assertEquals(SUCCESS, response.getStatus());
        verify(businessLayer).setDescription(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void emptyDescriptionIsForwarded() {
        LocationForSetDescription data = LocationForSetDescription.builder()
                .id(1)
                .version(2)
                .description("")
                .build();
        when(businessLayer.setDescription(data)).thenReturn(SUCCESS);

        Response response = uut.setDescription(createMockRequest(), data.id(), data.version(), data.description());

        assertEquals(SUCCESS, response.getStatus());
        verify(businessLayer).setDescription(data);
        Mockito.verify(businessLayer).setPrincipals(TEST_USER);
    }

    @Test
    public void errorFromBackendIsPropagated() {
        LocationForSetDescription data = LocationForSetDescription.builder()
                .id(1)
                .version(2)
                .description("new description")
                .build();
        when(businessLayer.setDescription(data)).thenReturn(INVALID_DATA_VERSION);

        Response response = uut.setDescription(createMockRequest(), data.id(), data.version(), data.description());

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
