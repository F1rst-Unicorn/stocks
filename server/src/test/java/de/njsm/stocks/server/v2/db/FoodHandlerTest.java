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
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;

public class FoodHandlerTest extends DbTestCase implements CrudOperationsTest<FoodRecord, Food> {

    private FoodHandler uut;

    @BeforeEach
    public void setup() {
        uut = new FoodHandler(getConnectionFactory());
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<Food>> result = uut.get(Instant.EPOCH);

        BitemporalFood sample = (BitemporalFood) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Override
    public FoodForInsertion getInsertable() {
        return FoodForInsertion.builder()
                .name("Banana")
                .storeUnit(1)
                .build();
    }

    @Test
    public void editAFood() {
        FoodForFullEditing data = FoodForFullEditing.builder()
                .id(2)
                .version(0)
                .name("Beer")
                .toBuy(false)
                .expirationOffset(3)
                .location(2)
                .storeUnit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void editFoodStoreUnit() {
        FoodForFullEditing data = FoodForFullEditing.builder()
                .id(2)
                .version(0)
                .name("Beer")
                .toBuy(false)
                .expirationOffset(3)
                .location(2)
                .storeUnit(2)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void editAFoodDefaultLocation() {
        FoodForFullEditing data = FoodForFullEditing.builder()
                .id(2)
                .version(0)
                .name("Beer")
                .toBuy(false)
                .expirationOffset(2)
                .location(2)
                .storeUnit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void editAFoodDefaultLocationBySettingNull() {
        FoodForFullEditing data = FoodForFullEditing.builder()
                .id(3)
                .version(0)
                .name("Cheese")
                .toBuy(false)
                .expirationOffset(3)
                .location(0)
                .storeUnit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void editAFoodExpirationOffset() {
        FoodForFullEditing data = FoodForFullEditing.builder()
                .id(3)
                .version(0)
                .name("Cheese")
                .toBuy(false)
                .expirationOffset(2)
                .location(1)
                .description("new description")
                .storeUnit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void editAFoodWithoutExpirationOrDefaultLocationDoesntUpdateThem() {
        FoodForFullEditing data = FoodForFullEditing.builder()
                .id(3)
                .version(0)
                .name("Cheddar")
                .toBuy(false)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void editingDescriptionWorks() {
        FoodForFullEditing data = FoodForFullEditing.builder()
                .id(2)
                .version(0)
                .name("Beer")
                .toBuy(false)
                .description("new description")
                .storeUnit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void wrongVersionIsNotRenamed() {
        FoodForFullEditing data = FoodForFullEditing.builder()
                .id(2)
                .version(100)
                .name("Wine")
                .toBuy(false)
                .expirationOffset(0)
                .location(2)
                .description("new description")
                .storeUnit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void unknownIsReported() {
        FoodForFullEditing data = FoodForFullEditing.builder()
                .id(100)
                .version(0)
                .name("Wine")
                .toBuy(false)
                .expirationOffset(0)
                .location(2)
                .description("new description")
                .storeUnit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void foodToBuyIsMarked() {
        FoodForSetToBuy data = FoodForSetToBuy.builder()
                .id(1)
                .version(0)
                .toBuy(true)
                .build();

        StatusCode result = uut.setToBuyStatus(data);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void foodToBuyWithInvalidVersionIsNotMarked() {
        FoodForSetToBuy data = FoodForSetToBuy.builder()
                .id(1)
                .version(2)
                .toBuy(true)
                .build();

        StatusCode result = uut.setToBuyStatus(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void missingFoodToBuyIsReported() {
        FoodForSetToBuy data = FoodForSetToBuy.builder()
                .id(100)
                .version(0)
                .toBuy(true)
                .build();

        StatusCode result = uut.setToBuyStatus(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void settingExplicitBuyStatusWorks() {
        FoodForSetToBuy data = FoodForSetToBuy.builder()
                .id(1)
                .version(0)
                .toBuy(true)
                .build();

        StatusCode result = uut.setToBuyStatus(data, false);

        assertEquals(StatusCode.SUCCESS, result);
        Food changedData = getCurrentData().stream().filter(f -> f.id() == data.id()).findFirst().get();
        assertFalse(changedData.toBuy());
    }

    @Test
    public void settingExplicitBuyStatusWithoutFindingAnyFoodIsOk() {
        FoodForSetToBuy data = FoodForSetToBuy.builder()
                .id(2)
                .version(1)
                .toBuy(true)
                .build();

        StatusCode result = uut.setToBuyStatus(data, true);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void unregisteringALocationWithoutFoodIsOk() {
        LocationForDeletion l = LocationForDeletion.builder()
                .id(2)
                .version(1)
                .build();

        StatusCode result = uut.unregisterDefaultLocation(l);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void unregisteringALocationWorks() {
        LocationForDeletion l = LocationForDeletion.builder()
                .id(1)
                .version(1)
                .build();

        StatusCode result = uut.unregisterDefaultLocation(l);

        assertEquals(StatusCode.SUCCESS, result);
        Food changedFood = getCurrentData().stream().filter(f -> f.id() == 3).findAny().get();
        assertNull(changedFood.location());
    }

    @Test
    public void settingDescriptionWorks() {
        FoodForSetDescription data = FoodForSetDescription.builder()
                .id(2)
                .version(0)
                .description("new description")
                .build();

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(getCurrentData().stream()
                .anyMatch(f -> f.id() == data.id() &&
                        data.version() + 1 == f.version() &&
                        data.description().equals(f.description())),
                () -> "expected description '" + data.description() + "' not found");
    }

    @Test
    public void settingDescriptionOnAbsentFoodIsReported() {
        FoodForSetDescription data = FoodForSetDescription.builder()
                .id(4)
                .version(0)
                .description("new description")
                .build();

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void settingDescriptionOnInvalidVersionIsReported() {
        FoodForSetDescription data = FoodForSetDescription.builder()
                .id(2)
                .version(1)
                .description("new description")
                .build();

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void settingDescriptionWithoutChangeIsPrevented() {
        FoodForSetDescription data = FoodForSetDescription.builder()
                .id(2)
                .version(0)
                .description("beer description")
                .build();

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Override
    public CrudDatabaseHandler<FoodRecord, Food> getDbHandler() {
        return uut;
    }

    @Override
    public int getNumberOfEntities() {
        return 3;
    }

    @Override
    public Versionable<Food> getUnknownEntity() {
        return FoodForDeletion.builder()
                .id(getNumberOfEntities() + 1)
                .version(0)
                .build();
    }

    @Override
    public Versionable<Food> getWrongVersionEntity() {
        return FoodForDeletion.builder()
                .id(getValidEntity().id())
                .version(getValidEntity().version() + 1)
                .build();
    }

    @Override
    public Versionable<Food> getValidEntity() {
        return FoodForDeletion.builder()
                .id(1)
                .version(0)
                .build();
    }
}
