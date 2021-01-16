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

import de.njsm.stocks.server.v2.business.FoodManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Food;
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
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.server.v2.business.StatusCode.*;
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
    public void puttingNullCodeIsInvalid() {

        Response result = uut.putFood(createMockRequest(), null);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void puttingEmptyCodeIsInvalid() {

        Response result = uut.putFood(createMockRequest(), "");

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void renamingInvalidIdIsInvalid() {

        Response result = uut.renameFood(createMockRequest(), 0, 1, "fdsa", 0, 1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void renamingInvalidVersionIsInvalid() {

        Response result = uut.renameFood(createMockRequest(), 1, -1, "fdsa", 0, 1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void renamingToInvalidNameIsInvalid() {

        Response result = uut.renameFood(createMockRequest(), 1, 1, "", 0, 1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void settingBuyStatusInvalidIdIsInvalid() {

        Response result = uut.setToBuyStatus(createMockRequest(), 0, 1, 1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void settingBuyStatusInvalidVersionIsInvalid() {

        Response result = uut.setToBuyStatus(createMockRequest(), 1, -1, 1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deletingInvalidIdIsInvalid() {

        Response result = uut.deleteFood(createMockRequest(), 0, 1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deletingInvalidVersionIsInvalid() {

        Response result = uut.deleteFood(createMockRequest(), 1, -1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void foodIsAdded() {
        Food data = new Food(0, "Banana", 0, false, Period.ZERO, null, "");
        when(manager.add(data)).thenReturn(Validation.success(5));

        Response response = uut.putFood(createMockRequest(), data.name);

        assertEquals(SUCCESS, response.status);
        verify(manager).add(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void getFoodReturnsList() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        List<Food> data = Collections.singletonList(new Food(2, "Banana", 2, true, Period.ZERO, 1, ""));
        when(manager.get(any(), eq(false), eq(Instant.EPOCH))).thenReturn(Validation.success(data.stream()));

        uut.get(r, 0, null);

        ArgumentCaptor<StreamResponse<Food>> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(SUCCESS, c.getValue().status);
        assertEquals(data, c.getValue().data.collect(Collectors.toList()));
        verify(manager).get(r, false, Instant.EPOCH);
    }

    @Test
    public void getFoodFromInvalidStartingPoint() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);

        uut.get(r, 1, "invalid");

        ArgumentCaptor<Response> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        TestCase.assertEquals(StatusCode.INVALID_ARGUMENT, c.getValue().status);
    }

    @Test
    public void renameFoodWorks() {
        String newName = "Bread";
        Food data = new Food(1, newName, 2, false, Period.ZERO.plusDays(3), 1, "");
        when(manager.rename(data)).thenReturn(SUCCESS);

        Response response = uut.renameFood(createMockRequest(), data.id, data.version, newName, 3, 1);

        assertEquals(SUCCESS, response.status);
        verify(manager).rename(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void renameFoodWithoutLocationYieldsNullAndWorks() {
        String newName = "Bread";
        Food data = new Food(1, newName, 2, false, Period.ZERO.plusDays(3), null, "");
        when(manager.rename(data)).thenReturn(SUCCESS);

        Response response = uut.renameFood(createMockRequest(), data.id, data.version, newName, 3, 0);

        assertEquals(SUCCESS, response.status);
        verify(manager).rename(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void settingBuyStatusWorks() {
        Food data = new Food(1, "", 2, true, Period.ZERO, 0, "");
        when(manager.setToBuyStatus(data)).thenReturn(SUCCESS);

        Response response = uut.setToBuyStatus(createMockRequest(), data.id, data.version, 1);

        assertEquals(SUCCESS, response.status);
        verify(manager).setToBuyStatus(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void deleteFoodWorks() {
        Food data = new Food(1, "", 2, false, Period.ZERO, 0, "");
        when(manager.delete(data)).thenReturn(SUCCESS);

        Response response = uut.deleteFood(createMockRequest(), data.id, data.version);

        assertEquals(SUCCESS, response.status);
        verify(manager).delete(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void settingDescriptionIsPropagated() {
        Food data = new Food(1, 2, "some description");
        when(manager.setDescription(data)).thenReturn(SUCCESS);

        Response response = uut.setDescription(createMockRequest(), data.id, data.version, data.description);

        assertEquals(SUCCESS, response.status);
        verify(manager).setDescription(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void errorFromBackendIsPropagated() {
        Food data = new Food(1, 2, "some description");
        when(manager.setDescription(data)).thenReturn(INVALID_DATA_VERSION);

        Response response = uut.setDescription(createMockRequest(), data.id, data.version, data.description);

        assertEquals(INVALID_DATA_VERSION, response.status);
        verify(manager).setDescription(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void invalidIdForDescriptionIsRejected() {
        Food data = new Food(1, 2, "some description");

        Response response = uut.setDescription(createMockRequest(), 0, data.version, data.description);

        assertEquals(INVALID_ARGUMENT, response.status);
    }

    @Test
    public void invalidVersionForDescriptionIsRejected() {
        Food data = new Food(1, 2, "some description");

        Response response = uut.setDescription(createMockRequest(), data.id, -1, data.description);

        assertEquals(INVALID_ARGUMENT, response.status);
    }
}
