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
        uut = new FoodHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<Food>> result = uut.get(true, Instant.EPOCH);

        BitemporalFood sample = (BitemporalFood) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Override
    public FoodForInsertion getInsertable() {
        return new FoodForInsertion("Banana", 1);
    }

    @Test
    public void editAFood() {
        FoodForEditing data = FoodForEditing.builder()
                .id(2)
                .version(0)
                .name("Beer")
                .expirationOffset(3)
                .location(2)
                .storeUnit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void editFoodStoreUnit() {
        FoodForEditing data = FoodForEditing.builder()
                .id(2)
                .version(0)
                .name("Beer")
                .expirationOffset(3)
                .location(2)
                .storeUnit(2)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void editAFoodDefaultLocation() {
        FoodForEditing data = FoodForEditing.builder()
                .id(2)
                .version(0)
                .name("Beer")
                .expirationOffset(2)
                .location(2)
                .storeUnit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void editAFoodDefaultLocationBySettingNull() {
        FoodForEditing data = FoodForEditing.builder()
                .id(3)
                .version(0)
                .name("Cheese")
                .expirationOffset(3)
                .location(0)
                .storeUnit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void editAFoodExpirationOffset() {
        FoodForEditing data = FoodForEditing.builder()
                .id(3)
                .version(0)
                .name("Cheese")
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
        FoodForEditing data = FoodForEditing.builder()
                .id(3)
                .version(0)
                .name("Cheddar")
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void editingDescriptionWorks() {
        FoodForEditing data = FoodForEditing.builder()
                .id(2)
                .version(0)
                .name("Beer")
                .description("new description")
                .storeUnit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void wrongVersionIsNotRenamed() {
        FoodForEditing data = FoodForEditing.builder()
                .id(2)
                .version(100)
                .name("Wine")
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
        FoodForEditing data = FoodForEditing.builder()
                .id(100)
                .version(0)
                .name("Wine")
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
        FoodForSetToBuy data = new FoodForSetToBuy(1, 0, true);

        StatusCode result = uut.setToBuyStatus(data);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void foodToBuyWithInvalidVersionIsNotMarked() {
        FoodForSetToBuy data = new FoodForSetToBuy(1, 2, true);

        StatusCode result = uut.setToBuyStatus(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void missingFoodToBuyIsReported() {
        FoodForSetToBuy data = new FoodForSetToBuy(100, 0, true);

        StatusCode result = uut.setToBuyStatus(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void settingExplicitBuyStatusWorks() {
        FoodForSetToBuy data = new FoodForSetToBuy(1, 0, true);

        StatusCode result = uut.setToBuyStatus(data, false);

        assertEquals(StatusCode.SUCCESS, result);
        Food changedData = uut.get(false, Instant.EPOCH).success().filter(f -> f.id() == data.id()).findFirst().get();
        assertFalse(changedData.toBuy());
    }

    @Test
    public void settingExplicitBuyStatusWithoutFindingAnyFoodIsOk() {
        FoodForSetToBuy data = new FoodForSetToBuy(1, 0, true);

        StatusCode result = uut.setToBuyStatus(data, true);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void unregisteringALocationWithoutFoodIsOk() {
        LocationForDeletion l = new LocationForDeletion(2, 1);

        StatusCode result = uut.unregisterDefaultLocation(l);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void unregisteringALocationWorks() {
        LocationForDeletion l = new LocationForDeletion(1, 1);

        StatusCode result = uut.unregisterDefaultLocation(l);

        assertEquals(StatusCode.SUCCESS, result);
        Food changedFood = uut.get(false, Instant.EPOCH).success().filter(f -> f.id() == 3).findAny().get();
        assertNull(changedFood.location());
    }

    @Test
    public void settingDescriptionWorks() {
        FoodForSetDescription data = new FoodForSetDescription(2, 0, "new description");

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(uut.get(false, Instant.EPOCH)
                .success()
                .anyMatch(f -> f.id() == data.id() &&
                        data.version() + 1 == f.version() &&
                        data.getDescription().equals(f.description())),
                () -> "expected description '" + data.getDescription() + "' not found");
    }

    @Test
    public void settingDescriptionOnAbsentFoodIsReported() {
        FoodForSetDescription data = new FoodForSetDescription(4, 0, "new description");

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void settingDescriptionOnInvalidVersionIsReported() {
        FoodForSetDescription data = new FoodForSetDescription(2, 1, "new description");

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void settingDescriptionWithoutChangeIsPrevented() {
        FoodForSetDescription data = new FoodForSetDescription(2, 0, "beer description");

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
                .id(2)
                .version(0)
                .build();
    }
}
