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

import de.njsm.stocks.common.api.FoodItem;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.StreamResponse;
import de.njsm.stocks.common.api.FoodItemForDeletion;
import de.njsm.stocks.common.api.FoodItemForEditing;
import de.njsm.stocks.common.api.FoodItemForInsertion;
import de.njsm.stocks.common.api.serialisers.InstantSerialiser;
import de.njsm.stocks.server.v2.business.FoodItemManager;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class FoodItemEndpointTest {

    private static final String DATE = "1970.01.01-00:00:00.000000-+0000";

    private FoodItemEndpoint uut;

    private FoodItemManager manager;

    @BeforeEach
    public void setup() {
        manager = Mockito.mock(FoodItemManager.class);
        uut = new FoodItemEndpoint(manager);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test
    public void testGettingItems() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(manager.get(r, Instant.EPOCH, Instant.EPOCH)).thenReturn(Validation.success(Stream.of()));

        uut.get(r, InstantSerialiser.serialize(Instant.EPOCH), InstantSerialiser.serialize(Instant.EPOCH));

        ArgumentCaptor<StreamResponse<FoodItem>> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(StatusCode.SUCCESS, c.getValue().getStatus());
        assertEquals(0, c.getValue().data.count());
        Mockito.verify(manager).get(r, Instant.EPOCH, Instant.EPOCH);
    }

    @Test
    public void getItemsFromInvalidStartingPoint() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);

        uut.get(r, "invalid", "invalid");

        ArgumentCaptor<Response> c = ArgumentCaptor.forClass(Response.class);
        verify(r).resume(c.capture());
        assertEquals(StatusCode.INVALID_ARGUMENT, c.getValue().getStatus());
    }

    @Test
    public void insertInvalidLocationIdIsReported() throws IOException {

        Response result = uut.putItem(Util.createMockRequest(), DATE, 0, 2, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void insertInvalidTypeIdIsReported() throws IOException {

        Response result = uut.putItem(Util.createMockRequest(), DATE, 2, 0, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void insertInvalidExpirationIdIsReported() throws IOException {

        Response result = uut.putItem(Util.createMockRequest(), "invalid date", 2, 0, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void validInsertHappens() throws IOException {
        FoodItemForInsertion expected = FoodItemForInsertion.builder()
                .eatByDate(Instant.EPOCH)
                .ofType(2)
                .storedIn(2)
                .registers(TEST_USER.getDid())
                .buys(TEST_USER.getUid())
                .unit(1)
                .build();
        Mockito.when(manager.add(expected)).thenReturn(Validation.success(1));

        Response result = uut.putItem(Util.createMockRequest(), DATE, expected.storedIn(), expected.ofType(), expected.unit().get());

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(manager).add(expected);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void editInvalidIdIsReported() throws IOException {

        Response result = uut.editItem(Util.createMockRequest(), 0, 2, DATE, 2, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void editInvalidVersionIdIsReported() throws IOException {

        Response result = uut.editItem(Util.createMockRequest(), 1, -1, DATE, 2, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void editInvalidDateIsReported() throws IOException {

        Response result = uut.editItem(Util.createMockRequest(), 2, 2, "invalid", 2, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void editInvalidLocationIsReported() throws IOException {

        Response result = uut.editItem(Util.createMockRequest(), 2, 2, DATE, 0, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void validEditingHappens() throws IOException {
        FoodItemForEditing expected = FoodItemForEditing.builder()
                .id(2)
                .version(2)
                .eatBy(Instant.EPOCH)
                .storedIn(2)
                .unit(1)
                .build();
        Mockito.when(manager.edit(expected)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.editItem(Util.createMockRequest(),
                expected.id(), expected.version(), DATE, expected.storedIn(), expected.unit().get());

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(manager).edit(expected);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void deleteInvalidIdIsReported() {

        Response result = uut.delete(createMockRequest(), 0, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deleteInvalidVersionIsReported() {

        Response result = uut.delete(createMockRequest(), 1, -1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void validDeletionHappens() {
        FoodItemForDeletion expected = FoodItemForDeletion.builder()
                .id(2)
                .version(2)
                .build();
        Mockito.when(manager.delete(expected)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.delete(createMockRequest(), expected.id(), expected.version());

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(manager).delete(expected);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }
}
