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
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.Period;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.Assert.*;

public class FoodHandlerTest extends DbTestCase {

    private FoodHandler uut;

    @Before
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
        assertNotNull(sample.getValidTimeStart());
        assertNotNull(sample.getValidTimeEnd());
        assertNotNull(sample.getTransactionTimeStart());
        assertNotNull(sample.getTransactionTimeEnd());
    }

    @Test
    public void addAFood() {
        FoodForInsertion data = new FoodForInsertion("Banana");

        Validation<StatusCode, Integer> code = uut.add(data);

        assertTrue(code.isSuccess());

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().anyMatch(f -> f.getName().equals("Banana")));
    }

    @Test
    public void editAFood() {
        FoodForEditing data = new FoodForEditing(2, 0, "Beer", Period.ofDays(3), 2);

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success()
                .anyMatch(f -> f.getName().equals(data.getNewName())
                        && f.getExpirationOffset().equals(data.getExpirationOffsetOptional().get())
                        && f.getLocation().equals(data.getLocationOptional().get())));
    }

    @Test
    public void editAFoodDefaultLocation() {
        FoodForEditing data = new FoodForEditing(2, 0, "Beer", Period.ZERO.plusDays(2), 2);

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success()
                .anyMatch(f -> f.getName().equals(data.getNewName())
                        && f.getExpirationOffset().equals(data.getExpirationOffsetOptional().get())
                        && f.getLocation().equals(data.getLocationOptional().get())));
    }

    @Test
    public void editAFoodDefaultLocationBySettingNull() {
        FoodForEditing data = new FoodForEditing(3, 0, "Cheese", Period.ZERO.plusDays(3), 0);

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success()
                .anyMatch(f -> f.getName().equals(data.getNewName())
                        && f.getExpirationOffset().equals(data.getExpirationOffsetOptional().get())
                        && f.getLocation() == null));
    }

    @Test
    public void editAFoodExpirationOffset() {
        FoodForEditing data = new FoodForEditing(3, 0, "Cheese", Period.ZERO.plusDays(2), 1);

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success()
                .anyMatch(f -> f.getName().equals(data.getNewName())
                        && f.getExpirationOffset().equals(data.getExpirationOffsetOptional().get())
                        && f.getLocation().equals(data.getLocationOptional().get())));
    }

    @Test
    public void editAFoodWithoutExpirationOrDefaultLocationDoesntUpdateThem() {
        FoodForEditing data = new FoodForEditing(3, 0, "Cheddar", null, null);

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success()
                .anyMatch(f -> f.getName().equals(data.getNewName())
                        && f.getExpirationOffset().equals(Period.ZERO.plusDays(3))
                        && f.getLocation() == 1));
    }

    @Test
    public void wrongVersionIsNotRenamed() {
        FoodForEditing data = new FoodForEditing(2, 100, "Wine", Period.ZERO, 2);

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void unknownIsReported() {
        FoodForEditing data = new FoodForEditing(100, 0, "Wine", Period.ZERO, 2);

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deleteAFood() {
        FoodForDeletion data = new FoodForDeletion(2, 0);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().map(Food::getName).noneMatch(name -> name.equals("Beer")));
    }

    @Test
    public void invalidDataVersionIsRejected() {
        FoodForDeletion data = new FoodForDeletion(2, 100);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertEquals(3, dbData.success().count());
    }

    @Test
    public void unknownDeletionsAreReported() {
        FoodForDeletion data = new FoodForDeletion(100, 0);

        StatusCode result = uut.delete(data);

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
        Food changedData = uut.get(false, Instant.EPOCH).success().filter(f -> f.getId() == data.getId()).findFirst().get();
        assertFalse(changedData.isToBuy());
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
        Food changedFood = uut.get(false, Instant.EPOCH).success().filter(f -> f.getId() == 3).findAny().get();
        assertNull(changedFood.getLocation());
    }

    @Test
    public void settingDescriptionWorks() {
        FoodForSetDescription data = new FoodForSetDescription(2, 0, "new description");

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.SUCCESS, result);
        assertTrue("expected description '" + data.getDescription() + "' not found",
                uut.get(false, Instant.EPOCH)
                .success()
                .anyMatch(f -> f.getId() == data.getId() &&
                        data.getVersion() + 1 == f.getVersion() &&
                        data.getDescription().equals(f.getDescription())));
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
}
