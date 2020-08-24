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
import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import fj.data.Validation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class FoodItemHandlerTest extends DbTestCase {

    private FoodItemHandler uut;

    private PresenceChecker<UserDevice> userDevicePresenceChecker;

    private PresenceChecker<User> userPresenceChecker;

    @Before
    public void setup() {
        userDevicePresenceChecker = (PresenceChecker<UserDevice>) Mockito.mock(PresenceChecker.class);
        userPresenceChecker = (PresenceChecker<User>) Mockito.mock(PresenceChecker.class);

        uut = new FoodItemHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT,
                new InsertVisitor<>(),
                userDevicePresenceChecker,
                userPresenceChecker);
    }

    @After
    public void verifyMocks() {
        Mockito.verifyNoMoreInteractions(userDevicePresenceChecker);
    }

    @Test
    public void testInserting() {
        FoodItem item = new FoodItem(1, 0, Instant.EPOCH, 2, 1, 1, 1);

        Validation<StatusCode, Integer> result = uut.add(item);

        Validation<StatusCode, Stream<FoodItem>> items = uut.get(false);
        Assert.assertTrue(result.isSuccess());
        assertTrue(items.isSuccess());
        assertEquals(4, items.success().count());
    }

    @Test
    public void testGettingItems() {

        Validation<StatusCode, Stream<FoodItem>> result = uut.get(false);

        assertTrue(result.isSuccess());
        List<FoodItem> list = result.success().collect(Collectors.toList());
        assertEquals(3, list.size());
        assertEquals(new FoodItem(1, 0, Instant.EPOCH, 2, 1, 3, 2), list.get(0));
        assertEquals(new FoodItem(2, 0, Instant.EPOCH, 2, 1, 3, 2), list.get(1));
        assertEquals(new FoodItem(3, 0, Instant.EPOCH, 2, 1, 3, 2), list.get(2));
    }

    @Test
    public void deletingUnknownIsReported() {

        StatusCode result = uut.delete(new FoodItem(4, 0));

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deletingWrongVersionIsReported() {

        StatusCode result = uut.delete(new FoodItem(1, 99));

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void validDeletionHappens() {

        StatusCode result = uut.delete(new FoodItem(1, 0));

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, Stream<FoodItem>> items = uut.get(false);
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.isSuccess());
        assertEquals(2, items.success().count());
    }

    @Test
    public void validEditingHappens() {
        FoodItem item = new FoodItem(1, 0, Instant.ofEpochMilli(42), 2, 2, 3, 2);

        StatusCode result = uut.edit(item);

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, Stream<FoodItem>> items = uut.get(false);
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.isSuccess());
        List<FoodItem> list = items.success().collect(Collectors.toList());
        assertEquals(3, list.size());
        item.version++;
        assertTrue(list.contains(item));
    }

    @Test
    public void editingWrongVersionIsReported() {
        FoodItem item = new FoodItem(1, 99, Instant.EPOCH, 2, 2, 3, 2);

        StatusCode result = uut.edit(item);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void editingUnknownIdIsReported() {
        FoodItem item = new FoodItem(99, 0, Instant.EPOCH, 2, 2, 3, 2);

        StatusCode result = uut.edit(item);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void movingFromUnknownDeviceIsReported() {
        UserDevice from = new UserDevice(1, 0, "fdsa", 1);
        UserDevice to = new UserDevice(3, 0, "fdsa", 2);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(true);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(from), any());
    }

    @Test
    public void movingToUnknownDeviceIsReported() {
        UserDevice from = new UserDevice(1, 0, "fdsa", 1);
        UserDevice to = new UserDevice(3, 0, "fdsa", 2);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(true);

        StatusCode result = uut.transferFoodItems(from, to);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(from), any());
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(to), any());
    }

    @Test
    public void successfulTransfer() {
        UserDevice from = new UserDevice(3, 0, "fdsa", 2);
        UserDevice to = new UserDevice(1, 0, "fdsa", 1);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to);

        Stream<FoodItem> items = uut.get(false).success();
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.allMatch(item -> (item.version == 1) == (item.registers == to.id)));
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(from), any());
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(to), any());
    }

    @Test
    public void movingFromUnknownUserIsReported() {
        User from = new User(1, 0, "fdsa");
        User to = new User(3, 0, "fdsa");
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(true);
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userPresenceChecker).isCurrentlyMissing(eq(from), any());
    }

    @Test
    public void movingToUnknownUserIsReported() {
        User from = new User(1, 0, "fdsa");
        User to = new User(3, 0, "fdsa");
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(true);

        StatusCode result = uut.transferFoodItems(from, to);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userPresenceChecker).isCurrentlyMissing(eq(from), any());
    }

    @Test
    public void moveUserSuccessfully() {
        User from = new User(3, 0, "fdsa");
        User to = new User(1, 0, "fdsa");
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to);

        Stream<FoodItem> items = uut.get(false).success();
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.allMatch(item -> (item.version == 1) == (item.registers == to.id)));
        Mockito.verify(userPresenceChecker).isCurrentlyMissing(eq(from), any());
        Mockito.verify(userPresenceChecker).isCurrentlyMissing(eq(to), any());
    }

    @Test
    public void deleteItemsInLocationWorks() {
        Location input = new Location(1, "", 0);

        StatusCode deleteResult = uut.deleteItemsStoredIn(input);

        Validation<StatusCode, Stream<FoodItem>> items = uut.get(false);

        assertEquals(StatusCode.SUCCESS, deleteResult);
        assertTrue(items.isSuccess());
        assertEquals(0, items.success().count());
    }

    @Test
    public void testingAreItemsStoredIn() {
        assertTrue(uut.areItemsStoredIn(new Location(1, "", 0), getDSLContext()));
        assertFalse(uut.areItemsStoredIn(new Location(2, "", 0), getDSLContext()));
    }
}
