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

import de.njsm.stocks.server.v2.business.FoodItemManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.business.data.FoodItemForDeletion;
import de.njsm.stocks.server.v2.business.data.FoodItemForEditing;
import de.njsm.stocks.server.v2.business.data.FoodItemForInsertion;
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
import java.io.IOException;
import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class FoodItemEndpointTest {

    private static final String DATE = "1970.01.01-00:00:00.000000-+0000";

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
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(manager.get(r, false, Instant.EPOCH)).thenReturn(Validation.success(Stream.of()));

        uut.get(r, 0, null);

        ArgumentCaptor<StreamResponse<FoodItem>> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(StatusCode.SUCCESS, c.getValue().getStatus());
        assertEquals(0, c.getValue().data.count());
        Mockito.verify(manager).get(r, false, Instant.EPOCH);
    }

    @Test
    public void getItemsFromInvalidStartingPoint() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);

        uut.get(r, 1, "invalid");

        ArgumentCaptor<Response> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        TestCase.assertEquals(StatusCode.INVALID_ARGUMENT, c.getValue().getStatus());
    }

    @Test
    public void insertInvalidLocationIdIsReported() throws IOException {

        Response result = uut.putItem(Util.createMockRequest(), DATE, 0, 2);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void insertInvalidTypeIdIsReported() throws IOException {

        Response result = uut.putItem(Util.createMockRequest(), DATE, 2, 0);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void insertInvalidExpirationIdIsReported() throws IOException {

        Response result = uut.putItem(Util.createMockRequest(), "invalid date", 2, 0);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void validInsertHappens() throws IOException {
        FoodItemForInsertion expected = new FoodItemForInsertion(Instant.EPOCH, 2, 2,
                PrincipalFilterTest.TEST_USER.getDid(),
                PrincipalFilterTest.TEST_USER.getUid());
        Mockito.when(manager.add(expected)).thenReturn(Validation.success(5));

        Response result = uut.putItem(Util.createMockRequest(), DATE, expected.getStoredIn(), expected.getOfType());

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(manager).add(expected);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void editInvalidIdIsReported() throws IOException {

        Response result = uut.editItem(Util.createMockRequest(), 0, 2, DATE, 2);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void editInvalidVersionIdIsReported() throws IOException {

        Response result = uut.editItem(Util.createMockRequest(), 1, -1, DATE, 2);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void editInvalidDateIsReported() throws IOException {

        Response result = uut.editItem(Util.createMockRequest(), 2, 2, "invalid", 2);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void editInvalidLocationIsReported() throws IOException {

        Response result = uut.editItem(Util.createMockRequest(), 2, 2, DATE, 0);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void validEditingHappens() throws IOException {
        FoodItemForEditing expected = new FoodItemForEditing(2, 2, Instant.EPOCH, 2);
        Mockito.when(manager.edit(expected)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.editItem(Util.createMockRequest(),
                expected.getId(), expected.getVersion(), DATE, expected.getStoredIn());

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(manager).edit(expected);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void deleteInvalidIdIsReported() {

        Response result = uut.deleteItem(createMockRequest(), 0, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deleteInvalidVersionIsReported() {

        Response result = uut.deleteItem(createMockRequest(), 1, -1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void validDeletionHappens() {
        FoodItemForDeletion expected = new FoodItemForDeletion(2, 2);
        Mockito.when(manager.delete(expected)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.deleteItem(createMockRequest(), expected.getId(), expected.getVersion());

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(manager).delete(expected);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }
}
