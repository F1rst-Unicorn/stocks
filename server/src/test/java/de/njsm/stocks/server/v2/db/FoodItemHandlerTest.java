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

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodItemRecord;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class FoodItemHandlerTest extends DbTestCase implements CrudOperationsTest<FoodItemRecord, FoodItem> {

    private FoodItemHandler uut;

    private PresenceChecker<UserDevice> userDevicePresenceChecker;

    private PresenceChecker<User> userPresenceChecker;

    @BeforeEach
    public void setup() {
        userDevicePresenceChecker = (PresenceChecker) Mockito.mock(PresenceChecker.class);
        userPresenceChecker = (PresenceChecker) Mockito.mock(PresenceChecker.class);

        uut = new FoodItemHandler(getConnectionFactory(),
                userDevicePresenceChecker,
                userPresenceChecker);
        uut.setPrincipals(TEST_USER);
    }

    @AfterEach
    public void verifyMocks() {
        Mockito.verifyNoMoreInteractions(userDevicePresenceChecker);
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<FoodItem>> result = uut.get(true, Instant.EPOCH);

        BitemporalFoodItem sample = (BitemporalFoodItem) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Test
    public void testGettingItems() {

        Validation<StatusCode, Stream<FoodItem>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<FoodItem> list = result.success().collect(Collectors.toList());
        assertEquals(3, list.size());
        assertEquals(FoodItemForGetting.builder()
                .id(1)
                .version(0)
                .eatByDate(Instant.EPOCH)
                .ofType(2)
                .storedIn(1)
                .registers(3)
                .buys(2)
                .unit(1)
                .build(), list.get(0));
        assertEquals(FoodItemForGetting.builder()
                .id(2)
                .version(0)
                .eatByDate(Instant.EPOCH)
                .ofType(2)
                .storedIn(1)
                .registers(3)
                .buys(2)
                .unit(1)
                .build(), list.get(1));
        assertEquals(FoodItemForGetting.builder()
                .id(3)
                .version(0)
                .eatByDate(Instant.EPOCH)
                .ofType(2)
                .storedIn(1)
                .registers(3)
                .buys(2)
                .unit(1)
                .build(), list.get(2));
    }

    @Test
    public void validEditingHappens() {
        FoodItemForEditing item = FoodItemForEditing.builder()
                .id(1)
                .version(0)
                .eatBy(Instant.ofEpochMilli(42))
                .storedIn(2)
                .unit(2)
                .build();

        StatusCode result = uut.edit(item);

        assertEditingWorked(item, result);
    }

    @Test
    public void editingUnitWorks() {
        FoodItemForEditing data = FoodItemForEditing.builder()
                .id(1)
                .version(0)
                .eatBy(Instant.EPOCH)
                .storedIn(1)
                .unit(2)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void editingWrongVersionIsReported() {
        FoodItemForEditing item = FoodItemForEditing.builder()
                .id(1)
                .version(99)
                .eatBy(Instant.EPOCH)
                .storedIn(2)
                .unit(1)
                .build();

        StatusCode result = uut.edit(item);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void editingUnknownIdIsReported() {
        FoodItemForEditing item = FoodItemForEditing.builder()
                .id(100)
                .version(0)
                .eatBy(Instant.EPOCH)
                .storedIn(2)
                .unit(1)
                .build();

        StatusCode result = uut.edit(item);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void movingFromUnknownDeviceIsReported() {
        UserDeviceForDeletion from = UserDeviceForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        UserDeviceForDeletion to = UserDeviceForDeletion.builder()
                .id(3)
                .version(0)
                .build();
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(true);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(from), any());
    }

    @Test
    public void movingToUnknownDeviceIsReported() {
        UserDeviceForDeletion from = UserDeviceForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        UserDeviceForDeletion to = UserDeviceForDeletion.builder()
                .id(3)
                .version(0)
                .build();
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(true);

        StatusCode result = uut.transferFoodItems(from, to);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(from), any());
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(to), any());
    }

    @Test
    public void successfulTransfer() {
        UserDeviceForDeletion from = UserDeviceForDeletion.builder()
                .id(3)
                .version(0)
                .build();
        UserDeviceForDeletion to = UserDeviceForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userDevicePresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to);

        Stream<FoodItem> items = uut.get(false, Instant.EPOCH).success();
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.allMatch(item -> (item.version() == 1) == (item.registers() == to.id())));
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(from), any());
        Mockito.verify(userDevicePresenceChecker).isCurrentlyMissing(eq(to), any());
    }

    @Test
    public void movingFromUnknownUserIsReported() {
        UserForDeletion from = UserForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        UserForDeletion to = UserForDeletion.builder()
                .id(2)
                .version(0)
                .build();
        UserDeviceForDeletion from1 = UserDeviceForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        UserDeviceForDeletion from2 = UserDeviceForDeletion.builder()
                .id(2)
                .version(0)
                .build();
        UserDeviceForDeletion toDevice = UserDeviceForDeletion.builder()
                .id(3)
                .version(0)
                .build();
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(true);
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to, Arrays.asList(from1, from2), toDevice);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userPresenceChecker).isCurrentlyMissing(eq(from), any());
    }

    @Test
    public void movingToUnknownUserIsReported() {
        UserForDeletion from = UserForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        UserForDeletion to = UserForDeletion.builder()
                .id(2)
                .version(0)
                .build();
        UserDeviceForDeletion from1 = UserDeviceForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        UserDeviceForDeletion from2 = UserDeviceForDeletion.builder()
                .id(2)
                .version(0)
                .build();
        UserDeviceForDeletion toDevice = UserDeviceForDeletion.builder()
                .id(3)
                .version(0)
                .build();
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(true);

        StatusCode result = uut.transferFoodItems(from, to, Arrays.asList(from1, from2), toDevice);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userPresenceChecker).isCurrentlyMissing(eq(from), any());
    }

    @Test
    public void moveUserSuccessfully() {
        UserForDeletion from = UserForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        UserForDeletion to = UserForDeletion.builder()
                .id(2)
                .version(0)
                .build();
        UserDeviceForDeletion from1 = UserDeviceForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        UserDeviceForDeletion from2 = UserDeviceForDeletion.builder()
                .id(2)
                .version(0)
                .build();
        UserDeviceForDeletion toDevice = UserDeviceForDeletion.builder()
                .id(3)
                .version(0)
                .build();
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userPresenceChecker.isCurrentlyMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to, Arrays.asList(from1, from2), toDevice);

        Stream<FoodItem> items = uut.get(false, Instant.EPOCH).success();
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.allMatch(item -> (item.version() == 1) == (item.registers() == to.id())));
        Mockito.verify(userPresenceChecker).isCurrentlyMissing(eq(from), any());
        Mockito.verify(userPresenceChecker).isCurrentlyMissing(eq(to), any());
    }

    @Test
    public void deleteItemsInLocationWorks() {
        LocationForDeletion input = LocationForDeletion.builder()
                .id(1)
                .version(0)
                .build();

        StatusCode deleteResult = uut.deleteItemsStoredIn(input);

        Validation<StatusCode, Stream<FoodItem>> items = uut.get(false, Instant.EPOCH);

        assertEquals(StatusCode.SUCCESS, deleteResult);
        assertTrue(items.isSuccess());
        assertEquals(0, items.success().count());
    }

    @Test
    public void testingAreItemsStoredIn() {
        assertTrue(uut.areItemsStoredIn(LocationForDeletion.builder()
                .id(1)
                .version(0)
                .build(), getDSLContext()));
        assertFalse(uut.areItemsStoredIn(LocationForDeletion.builder()
                .id(2)
                .version(0)
                .build(), getDSLContext()));
    }

    @Test
    public void deletingFoodWithoutItemsIsOk() {

        StatusCode result = uut.deleteItemsOfType(FoodForDeletion.builder()
                .id(1)
                .version(1)
                .build());

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void deletingCodesWorks() {
        long entities = uut.get(false, Instant.EPOCH).success().count();
        assertEquals(3, entities);

        StatusCode result = uut.deleteItemsOfType(FoodForDeletion.builder()
                .id(2)
                .version(1)
                .build());

        assertEquals(StatusCode.SUCCESS, result);
        entities = uut.get(false, Instant.EPOCH).success().count();
        assertEquals(0, entities);
    }

    @Override
    public CrudDatabaseHandler<FoodItemRecord, FoodItem> getDbHandler() {
        return uut;
    }

    @Override
    public Insertable<FoodItem> getInsertable() {
        return FoodItemForInsertion.builder()
                .eatByDate(Instant.EPOCH)
                .ofType(2)
                .storedIn(1)
                .registers(1)
                .buys(1)
                .unit(1)
                .build();
    }

    @Override
    public int getNumberOfEntities() {
        return 3;
    }

    @Override
    public Versionable<FoodItem> getUnknownEntity() {
        return FoodItemForDeletion.builder()
                .id(getNumberOfEntities() + 1)
                .version(0)
                .build();
    }

    @Override
    public Versionable<FoodItem> getWrongVersionEntity() {
        return FoodItemForDeletion.builder()
                .id(getValidEntity().id())
                .version(getValidEntity().version() + 1)
                .build();
    }

    @Override
    public Versionable<FoodItem> getValidEntity() {
        return FoodItemForDeletion.builder()
                .id(1)
                .version(0)
                .build();
    }
}
