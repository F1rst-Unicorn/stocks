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
import de.njsm.stocks.server.v2.business.FoodManager;
import fj.data.Validation;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.common.api.StatusCode.*;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FoodEndpointTest {

    private FoodEndpoint uut;

    private FoodManager manager;

    @Before
    public void setup() {
        manager = Mockito.mock(FoodManager.class);
        uut = new FoodEndpoint(manager);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test
    public void puttingNullNameIsInvalid() {

        Response result = uut.putFood(createMockRequest(), null, null);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void puttingEmptyNameIsInvalid() {

        Response result = uut.putFood(createMockRequest(), "", null);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void renamingInvalidIdIsInvalid() {

        Response result = uut.edit(createMockRequest(), 0, 1, "fdsa", 0, 1, "", 1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void renamingInvalidVersionIsInvalid() {

        Response result = uut.edit(createMockRequest(), 1, -1, "fdsa", 0, 1, "", 1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void renamingToInvalidNameIsInvalid() {

        Response result = uut.edit(createMockRequest(), 1, 1, "", 0, 1, "", 1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void settingBuyStatusInvalidIdIsInvalid() {

        Response result = uut.setToBuyStatus(createMockRequest(), 0, 1, 1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void settingBuyStatusInvalidVersionIsInvalid() {

        Response result = uut.setToBuyStatus(createMockRequest(), 1, -1, 1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deletingInvalidIdIsInvalid() {

        Response result = uut.delete(createMockRequest(), 0, 1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deletingInvalidVersionIsInvalid() {

        Response result = uut.delete(createMockRequest(), 1, -1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void foodIsAdded() {
        FoodForInsertion data = new FoodForInsertion("Banana", 1);
        when(manager.add(data)).thenReturn(SUCCESS);

        Response response = uut.putFood(createMockRequest(), data.getName(), data.getStoreUnit().orElseThrow());

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).add(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void getFoodReturnsList() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        FoodForGetting food = FoodForGetting.builder()
                .id(2)
                .version(2)
                .name("Banana")
                .toBuy(true)
                .expirationOffset(Period.ZERO)
                .location(1)
                .description("")
                .storeUnit(1)
                .build();
        List<Food> data = Collections.singletonList(food);
        when(manager.get(any(), eq(false), eq(Instant.EPOCH))).thenReturn(Validation.success(data.stream()));

        uut.get(r, 0, null);

        ArgumentCaptor<StreamResponse<Food>> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(SUCCESS, c.getValue().getStatus());
        assertEquals(data, c.getValue().data.collect(Collectors.toList()));
        verify(manager).get(r, false, Instant.EPOCH);
    }

    @Test
    public void getFoodFromInvalidStartingPoint() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);

        uut.get(r, 1, "invalid");

        ArgumentCaptor<Response> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        TestCase.assertEquals(INVALID_ARGUMENT, c.getValue().getStatus());
    }

    @Test
    public void editWorks() {
        FoodForEditing data = new FoodForEditing(1, 2, "Bread", Period.ZERO, 2, "new description", 1);
        when(manager.rename(data)).thenReturn(SUCCESS);

        Response response = uut.edit(createMockRequest(),
                data.id(),
                data.version(),
                data.getNewName(),
                data.getExpirationOffsetOptional().get().getDays(),
                data.getLocationOptional().get(),
                data.getDescription().get(),
                data.getStoreUnit().get());

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).rename(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void editWithoutLocationWorks() {
        FoodForEditing data = new FoodForEditing(1, 2, "Bread", Period.ZERO, 0, "new description", 1);
        when(manager.rename(data)).thenReturn(SUCCESS);

        Response response = uut.edit(createMockRequest(),
                data.id(),
                data.version(),
                data.getNewName(),
                data.getExpirationOffsetOptional().get().getDays(),
                data.getLocationOptional().get(),
                data.getDescription().get(),
                data.getStoreUnit().get());

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).rename(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void editWithoutLocationYieldsNullAndMapsCorrectly() {
        FoodForEditing data = new FoodForEditing(1, 2, "Bread", Period.ZERO, null, "new description", 1);
        when(manager.rename(data)).thenReturn(SUCCESS);

        Response response = uut.edit(createMockRequest(),
                data.id(),
                data.version(),
                data.getNewName(),
                data.getExpirationOffsetOptional().get().getDays(),
                null,
                data.getDescription().get(),
                data.getStoreUnit().get());

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).rename(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void editWithoutExpirationOffsetYieldsNullAndMapsCorrectly() {
        FoodForEditing data = new FoodForEditing(1, 2, "Bread", null, 2, "new description", 1);
        when(manager.rename(data)).thenReturn(SUCCESS);

        Response response = uut.edit(createMockRequest(),
                data.id(),
                data.version(),
                data.getNewName(),
                null,
                data.getLocationOptional().get(),
                data.getDescription().get(),
                data.getStoreUnit().get());

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).rename(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void settingBuyStatusWorks() {
        FoodForSetToBuy data = new FoodForSetToBuy(1, 2, true);
        when(manager.setToBuyStatus(data)).thenReturn(SUCCESS);

        Response response = uut.setToBuyStatus(createMockRequest(),
                data.id(),
                data.version(),
                data.isToBuy() ? 1 : 0);

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).setToBuyStatus(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void deleteWorks() {
        FoodForDeletion data = new FoodForDeletion(1, 2);
        when(manager.delete(data)).thenReturn(SUCCESS);

        Response response = uut.delete(createMockRequest(), data.id(), data.version());

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).delete(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void settingDescriptionIsPropagated() {
        FoodForSetDescription data = new FoodForSetDescription(1, 2, "some description");
        when(manager.setDescription(data)).thenReturn(SUCCESS);

        Response response = uut.setDescription(createMockRequest(), data.id(), data.version(), data.getDescription());

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).setDescription(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void emptyDescriptionIsPropagated() {
        FoodForSetDescription data = new FoodForSetDescription(1, 2, "");
        when(manager.setDescription(data)).thenReturn(SUCCESS);

        Response response = uut.setDescription(createMockRequest(), data.id(), data.version(), data.getDescription());

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).setDescription(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void errorFromBackendIsPropagated() {
        FoodForSetDescription data = new FoodForSetDescription(1, 2, "some description");
        when(manager.setDescription(data)).thenReturn(INVALID_DATA_VERSION);

        Response response = uut.setDescription(createMockRequest(), data.id(), data.version(), data.getDescription());

        assertEquals(INVALID_DATA_VERSION, response.getStatus());
        verify(manager).setDescription(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void invalidIdForDescriptionIsRejected() {
        Response response = uut.setDescription(createMockRequest(), 0, 2, "");
        assertEquals(INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void invalidVersionForDescriptionIsRejected() {
        Response response = uut.setDescription(createMockRequest(), 1, -1, "");
        assertEquals(INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void invalidDescriptionIsRejected() {
        Response response = uut.setDescription(createMockRequest(), 1, 2, null);
        assertEquals(INVALID_ARGUMENT, response.getStatus());
    }
}
