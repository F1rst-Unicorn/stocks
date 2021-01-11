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
import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.business.data.Location;
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
                CIRCUIT_BREAKER_TIMEOUT,
                new InsertVisitor<>());
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<Food>> result = uut.get(true, Instant.EPOCH);

        Food sample = result.success().findAny().get();
        assertNotNull(sample.validTimeStart);
        assertNotNull(sample.validTimeEnd);
        assertNotNull(sample.transactionTimeStart);
        assertNotNull(sample.transactionTimeEnd);
    }

    @Test
    public void addAFood() {
        Period expirationOffset = Period.ofDays(3);
        Food data = new Food(7, "Banana", 1, false, expirationOffset, 0, "");

        Validation<StatusCode, Integer> code = uut.add(data);

        assertTrue(code.isSuccess());

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().anyMatch(f -> f.name.equals("Banana") && f.expirationOffset.equals(expirationOffset)));
    }

    @Test
    public void editAFood() {
        String newName = "Wine";
        Period expirationOffset = Period.ofDays(3);
        int location = 2;
        Food data = new Food(2, "Beer", 0, true, Period.ZERO, location, "");

        StatusCode result = uut.edit(data, newName, expirationOffset, location);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success()
                .anyMatch(f -> f.name.equals(newName)
                        && f.toBuy
                        && f.expirationOffset.equals(expirationOffset)
                        && f.location == location));
    }

    @Test
    public void editAFoodDefaultLocation() {
        String newName = "Beer";
        Period expirationOffset = Period.ZERO;
        int location = 2;
        Food data = new Food(2, newName, 0, true, expirationOffset, null, "");

        StatusCode result = uut.edit(data, newName, expirationOffset, location);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success()
                .anyMatch(f -> f.name.equals(newName)
                        && f.toBuy
                        && f.expirationOffset.equals(expirationOffset)
                        && f.location == location));
    }

    @Test
    public void wrongVersionIsNotRenamed() {
        String newName = "Wine";
        Food data = new Food(2, "Beer", 100, true, Period.ZERO, 1, "");

        StatusCode result = uut.edit(data, newName, Period.ZERO, 1);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void unknownIsReported() {
        String newName = "Wine";
        Food data = new Food(100, "Beer", 1, true, Period.ZERO, 1, "");

        StatusCode result = uut.edit(data, newName, Period.ZERO, 1);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deleteAFood() {
        Food data = new Food(2, "Beer", 0, true, Period.ZERO, 1, "");

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().map(f -> f.name).noneMatch(name -> name.equals("Beer")));
    }

    @Test
    public void invalidDataVersionIsRejected() {
        Food data = new Food(2, "Beer", 100, true, Period.ZERO, 1, "");

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);

        Validation<StatusCode, Stream<Food>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertEquals(3, dbData.success().count());
    }

    @Test
    public void unknownDeletionsAreReported() {
        Food data = new Food(100, "Beer", 1, true, Period.ZERO, 1, "");

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void foodToBuyIsMarked() {
        Food data = new Food(1, "Carrot", 0, true, Period.ZERO, 1, "");

        StatusCode result = uut.setToBuyStatus(data);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void foodToBuyWithInvalidVersionIsNotMarked() {
        Food data = new Food(1, "Carrot", 2, true, Period.ZERO, 1, "");

        StatusCode result = uut.setToBuyStatus(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void missingFoodToBuyIsReported() {
        Food data = new Food(100, "Beer", 1, true, Period.ZERO, 1, "");

        StatusCode result = uut.setToBuyStatus(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void settingExplicitBuyStatusWorks() {
        Food data = new Food(2, 1);

        StatusCode result = uut.setToBuyStatus(data, false);

        assertEquals(StatusCode.SUCCESS, result);
        Food changedData = uut.get(false, Instant.EPOCH).success().filter(f -> f.id == data.id).findFirst().get();
        assertFalse(changedData.toBuy);
    }

    @Test
    public void settingExplicitBuyStatusWithoutFindingAnyFoodIsOk() {
        Food data = new Food(2, 1);

        StatusCode result = uut.setToBuyStatus(data, true);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void unregisteringALocationWithoutFoodIsOk() {
        Location l = new Location(2, "", 1);

        StatusCode result = uut.unregisterDefaultLocation(l);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void unregisteringALocationWorks() {
        Location l = new Location(1, "", 1);

        StatusCode result = uut.unregisterDefaultLocation(l);

        assertEquals(StatusCode.SUCCESS, result);
        Food changedFood = uut.get(false, Instant.EPOCH).success().filter(f -> f.id == 3).findAny().get();
        assertNull(changedFood.location);
    }

    @Test
    public void settingDescriptionWorks() {
        Food data = new Food(2, 0, "new description");

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.SUCCESS, result);
        assertTrue("expected description '" + data.description + "' not found",
                uut.get(false, Instant.EPOCH)
                .success()
                .anyMatch(f -> f.id == data.id &&
                        data.version + 1 == f.version &&
                        data.description.equals(f.description)));
    }

    @Test
    public void settingDescriptionOnAbsentFoodIsReported() {
        Food data = new Food(4, 0, "new description");

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }
}
