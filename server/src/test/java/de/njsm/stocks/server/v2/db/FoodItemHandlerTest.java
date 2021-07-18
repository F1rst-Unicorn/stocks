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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.*;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.matchers.Matchers.matchesInsertable;
import static de.njsm.stocks.server.v2.matchers.Matchers.matchesVersionable;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class FoodItemHandlerTest extends DbTestCase {

    private FoodItemHandler uut;

    private PresenceChecker<UserDevice> userDevicePresenceChecker;

    private PresenceChecker<User> userPresenceChecker;

    @Before
    public void setup() {
        userDevicePresenceChecker = (PresenceChecker) Mockito.mock(PresenceChecker.class);
        userPresenceChecker = (PresenceChecker) Mockito.mock(PresenceChecker.class);

        uut = new FoodItemHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT,
                userDevicePresenceChecker,
                userPresenceChecker);
        uut.setPrincipals(TEST_USER);
    }

    @After
    public void verifyMocks() {
        Mockito.verifyNoMoreInteractions(userDevicePresenceChecker);
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<FoodItem>> result = uut.get(true, Instant.EPOCH);

        BitemporalFoodItem sample = (BitemporalFoodItem) result.success().findAny().get();
        assertNotNull(sample.getValidTimeStart());
        assertNotNull(sample.getValidTimeEnd());
        assertNotNull(sample.getTransactionTimeStart());
        assertNotNull(sample.getTransactionTimeEnd());
    }

    @Test
    public void testInserting() {
        FoodItemForInsertion item = new FoodItemForInsertion(Instant.EPOCH, 2, 1, 1, 1, 1);

        StatusCode result = uut.add(item);

        Validation<StatusCode, Stream<FoodItem>> items = uut.get(false, Instant.EPOCH);
        assertTrue(result.isSuccess());
        assertTrue(items.isSuccess());

        List<FoodItem> list = items.success().collect(Collectors.toList());
        assertEquals(4, list.size());
        assertThat(list, hasItem(matchesInsertable(item)));
    }

    @Test
    public void testGettingItems() {

        Validation<StatusCode, Stream<FoodItem>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<FoodItem> list = result.success().collect(Collectors.toList());
        assertEquals(3, list.size());
        assertEquals(new FoodItemForGetting(1, 0, Instant.EPOCH, 2, 1, 3, 2, 1), list.get(0));
        assertEquals(new FoodItemForGetting(2, 0, Instant.EPOCH, 2, 1, 3, 2, 1), list.get(1));
        assertEquals(new FoodItemForGetting(3, 0, Instant.EPOCH, 2, 1, 3, 2, 1), list.get(2));
    }

    @Test
    public void deletingUnknownIsReported() {

        StatusCode result = uut.delete(new FoodItemForDeletion(4, 0));

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deletingWrongVersionIsReported() {

        StatusCode result = uut.delete(new FoodItemForDeletion(1, 99));

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void validDeletionHappens() {

        StatusCode result = uut.delete(new FoodItemForDeletion(1, 0));

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, Stream<FoodItem>> items = uut.get(false, Instant.EPOCH);
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.isSuccess());
        assertEquals(2, items.success().count());
    }

    @Test
    public void validEditingHappens() {
        FoodItemForEditing item = new FoodItemForEditing(1, 0, Instant.ofEpochMilli(42), 2, 2);

        StatusCode result = uut.edit(item);

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, Stream<FoodItem>> items = uut.get(false, Instant.EPOCH);
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.isSuccess());
        List<FoodItem> list = items.success().collect(Collectors.toList());
        assertEquals(3, list.size());
        assertThat(list, hasItem(new FoodItemForGetting(1, 1, item.getEatBy(), 2, item.getStoredIn(), 3, 2, 2)));
    }

    @Test
    public void editingUnitWorks() {
        FoodItemForEditing data = new FoodItemForEditing(1, 0, Instant.EPOCH, 1, 2);

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    private void assertEditingWorked(FoodItemForEditing data, StatusCode result) {
        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, Stream<FoodItem>> dbData = uut.get(false, Instant.EPOCH);
        assertTrue(dbData.isSuccess());
        List<FoodItem> currentData = dbData.success().collect(Collectors.toList());
        assertThat(currentData, hasItem(matchesVersionable(data)));
    }

    @Test
    public void editingWrongVersionIsReported() {
        FoodItemForEditing item = new FoodItemForEditing(1, 99, Instant.ofEpochMilli(42), 2, 1);

        StatusCode result = uut.edit(item);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void editingUnknownIdIsReported() {
        FoodItemForEditing item = new FoodItemForEditing(100, 0, Instant.ofEpochMilli(42), 2, 1);

        StatusCode result = uut.edit(item);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void movingFromUnknownDeviceIsReported() {
        UserDeviceForDeletion from = new UserDeviceForDeletion(1, 0);
        UserDeviceForDeletion to = new UserDeviceForDeletion(3, 0);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(true);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(from), any());
    }

    @Test
    public void movingToUnknownDeviceIsReported() {
        UserDeviceForDeletion from = new UserDeviceForDeletion(1, 0);
        UserDeviceForDeletion to = new UserDeviceForDeletion(3, 0);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(true);

        StatusCode result = uut.transferFoodItems(from, to);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(from), any());
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(to), any());
    }

    @Test
    public void successfulTransfer() {
        UserDeviceForDeletion from = new UserDeviceForDeletion(3, 0);
        UserDeviceForDeletion to = new UserDeviceForDeletion(1, 0);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to);

        Stream<FoodItem> items = uut.get(false, Instant.EPOCH).success();
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.allMatch(item -> (item.getVersion() == 1) == (item.getRegisters() == to.getId())));
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(from), any());
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(to), any());
    }

    @Test
    public void movingFromUnknownUserIsReported() {
        UserForDeletion from = new UserForDeletion(1, 0);
        UserForDeletion to = new UserForDeletion(2, 0);
        UserDeviceForDeletion from1 = new UserDeviceForDeletion(1, 0);
        UserDeviceForDeletion from2 = new UserDeviceForDeletion(2, 0);
        UserDeviceForDeletion toDevice = new UserDeviceForDeletion(3, 0);
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(true);
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to, Arrays.asList(from1, from2), toDevice);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userPresenceChecker).isCurrentlyMissing(eq(from), any());
    }

    @Test
    public void movingToUnknownUserIsReported() {
        UserForDeletion from = new UserForDeletion(1, 0);
        UserForDeletion to = new UserForDeletion(2, 0);
        UserDeviceForDeletion from1 = new UserDeviceForDeletion(1, 0);
        UserDeviceForDeletion from2 = new UserDeviceForDeletion(2, 0);
        UserDeviceForDeletion toDevice = new UserDeviceForDeletion(3, 0);
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(true);

        StatusCode result = uut.transferFoodItems(from, to, Arrays.asList(from1, from2), toDevice);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userPresenceChecker).isCurrentlyMissing(eq(from), any());
    }

    @Test
    public void moveUserSuccessfully() {
        UserForDeletion from = new UserForDeletion(1, 0);
        UserForDeletion to = new UserForDeletion(2, 0);
        UserDeviceForDeletion from1 = new UserDeviceForDeletion(1, 0);
        UserDeviceForDeletion from2 = new UserDeviceForDeletion(2, 0);
        UserDeviceForDeletion toDevice = new UserDeviceForDeletion(3, 0);
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to, Arrays.asList(from1, from2), toDevice);

        Stream<FoodItem> items = uut.get(false, Instant.EPOCH).success();
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.allMatch(item -> (item.getVersion() == 1) == (item.getRegisters() == to.getId())));
        Mockito.verify(userPresenceChecker).isCurrentlyMissing(eq(from), any());
        Mockito.verify(userPresenceChecker).isCurrentlyMissing(eq(to), any());
    }

    @Test
    public void deleteItemsInLocationWorks() {
        LocationForDeletion input = new LocationForDeletion(1, 0);

        StatusCode deleteResult = uut.deleteItemsStoredIn(input);

        Validation<StatusCode, Stream<FoodItem>> items = uut.get(false, Instant.EPOCH);

        assertEquals(StatusCode.SUCCESS, deleteResult);
        assertTrue(items.isSuccess());
        assertEquals(0, items.success().count());
    }

    @Test
    public void testingAreItemsStoredIn() {
        assertTrue(uut.areItemsStoredIn(new LocationForDeletion(1, 0), getDSLContext()));
        assertFalse(uut.areItemsStoredIn(new LocationForDeletion(2, 0), getDSLContext()));
    }

    @Test
    public void deletingFoodWithoutItemsIsOk() {

        StatusCode result = uut.deleteItemsOfType(new FoodForDeletion(1, 1));

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void deletingCodesWorks() {
        long entities = uut.get(false, Instant.EPOCH).success().count();
        assertEquals(3, entities);

        StatusCode result = uut.deleteItemsOfType(new FoodForDeletion(2, 1));

        assertEquals(StatusCode.SUCCESS, result);
        entities = uut.get(false, Instant.EPOCH).success().count();
        assertEquals(0, entities);
    }
}
