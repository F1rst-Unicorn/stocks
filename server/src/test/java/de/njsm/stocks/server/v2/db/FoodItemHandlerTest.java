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
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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

        Validation<StatusCode, Stream<FoodItem>> result = uut.get(Instant.EPOCH, INFINITY.toInstant());

        BitemporalFoodItem sample = (BitemporalFoodItem) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Test
    public void testGettingItems() {

        var data = getCurrentData();

        assertEquals(3, data.size());
        assertTrue(data.stream().anyMatch(v ->
                v.id() == 1 &&
                v.version() == 0 &&
                v.eatByDate().equals(Instant.EPOCH) &&
                v.ofType() == 2 &&
                v.storedIn() == 1 &&
                v.registers() == 3 &&
                v.buys() == 2 &&
                v.unit() == 1));
        assertTrue(data.stream().anyMatch(v ->
                v.id() == 2 &&
                v.version() == 0 &&
                v.eatByDate().equals(Instant.EPOCH) &&
                v.ofType() == 2 &&
                v.storedIn() == 1 &&
                v.registers() == 3 &&
                v.buys() == 2 &&
                v.unit() == 1));
        assertTrue(data.stream().anyMatch(v ->
                v.id() == 3 &&
                v.version() == 0 &&
                v.eatByDate().equals(Instant.EPOCH) &&
                v.ofType() == 2 &&
                v.storedIn() == 1 &&
                v.registers() == 3 &&
                v.buys() == 2 &&
                v.unit() == 1));
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

        Stream<FoodItem> items = getCurrentData().stream();
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

        Stream<FoodItem> items = getCurrentData().stream();
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

        Stream<FoodItem> items = getCurrentData().stream();

        assertEquals(StatusCode.SUCCESS, deleteResult);
        assertEquals(0, items.count());
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
        long entities = getCurrentData().size();
        assertEquals(3, entities);

        StatusCode result = uut.deleteItemsOfType(FoodForDeletion.builder()
                .id(2)
                .version(1)
                .build());

        assertEquals(StatusCode.SUCCESS, result);
        entities = getCurrentData().size();
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
