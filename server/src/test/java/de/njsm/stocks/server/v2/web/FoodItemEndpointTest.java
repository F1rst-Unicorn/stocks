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

import de.njsm.stocks.server.v1.endpoints.BaseTestEndpoint;
import de.njsm.stocks.server.v2.business.FoodItemManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class FoodItemEndpointTest {

    private static final String DATE = "1970.01.01-00:00:00.000-+0000";

    private FoodItemEndpoint uut;

    private FoodItemManager manager;

    @Before
    public void setup() {
        manager = Mockito.mock(FoodItemManager.class);
        uut = new FoodItemEndpoint(manager);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test
    public void testGettingItems() {
        Mockito.when(manager.get()).thenReturn(Validation.success(new LinkedList<>()));

        ListResponse<FoodItem> result = uut.getItems();

        assertEquals(StatusCode.SUCCESS, result.status);
        assertEquals(0, result.data.size());
        Mockito.verify(manager).get();
    }

    @Test
    public void insertInvalidLocationIdIsReported() throws IOException {

        Response result = uut.putItem(BaseTestEndpoint.createMockRequest(), DATE, 0, 2);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void insertInvalidTypeIdIsReported() throws IOException {

        Response result = uut.putItem(BaseTestEndpoint.createMockRequest(), DATE, 2, 0);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void insertInvalidExpirationIdIsReported() throws IOException {

        Response result = uut.putItem(BaseTestEndpoint.createMockRequest(), "invalid date", 2, 0);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void validInsertHappens() throws IOException {
        FoodItem expected = new FoodItem(0, 0, Instant.EPOCH, 2, 2,
                PrincipalFilterTest.TEST_USER.getDid(),
                PrincipalFilterTest.TEST_USER.getUid());
        Mockito.when(manager.add(expected)).thenReturn(Validation.success(5));

        Response result = uut.putItem(BaseTestEndpoint.createMockRequest(), DATE, expected.storedIn, expected.ofType);

        assertEquals(StatusCode.SUCCESS, result.status);
        Mockito.verify(manager).add(expected);
    }

    @Test
    public void editInvalidIdIsReported() throws IOException {

        Response result = uut.editItem(BaseTestEndpoint.createMockRequest(), 0, 2, DATE, 2);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void editInvalidVersionIdIsReported() throws IOException {

        Response result = uut.editItem(BaseTestEndpoint.createMockRequest(), 1, -1, DATE, 2);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void editInvalidDateIsReported() throws IOException {

        Response result = uut.editItem(BaseTestEndpoint.createMockRequest(), 2, 2, "invalid", 2);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void editInvalidLocationIsReported() throws IOException {

        Response result = uut.editItem(BaseTestEndpoint.createMockRequest(), 2, 2, DATE, 0);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void validEditingHappens() throws IOException {
        FoodItem expected = new FoodItem(2, 2, Instant.EPOCH, 0, 2,
                PrincipalFilterTest.TEST_USER.getDid(),
                PrincipalFilterTest.TEST_USER.getUid());
        Mockito.when(manager.edit(expected)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.editItem(BaseTestEndpoint.createMockRequest(),
                expected.id, expected.version, DATE, expected.storedIn);

        assertEquals(StatusCode.SUCCESS, result.status);
        Mockito.verify(manager).edit(expected);
    }

    @Test
    public void deleteInvalidIdIsReported() {

        Response result = uut.deleteItem(0, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deleteInvalidVersionIsReported() {

        Response result = uut.deleteItem(1, -1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void validDeletionHappens() {
        FoodItem expected = new FoodItem(2, 2, Instant.EPOCH, 0, 0, 0, 0);
        Mockito.when(manager.delete(expected)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.deleteItem(expected.id, expected.version);

        assertEquals(StatusCode.SUCCESS, result.status);
        Mockito.verify(manager).delete(expected);
    }
}