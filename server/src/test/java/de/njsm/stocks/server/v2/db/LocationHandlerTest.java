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
import de.njsm.stocks.server.v2.business.data.Location;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

public class LocationHandlerTest extends DbTestCase {

    private LocationHandler uut;

    private FoodItemHandler foodItemHandler;

    @Before
    public void setup() {
        foodItemHandler = Mockito.mock(FoodItemHandler.class);

        uut = new LocationHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT,
                new InsertVisitor<>(),
                foodItemHandler);
    }

    @Test
    public void addALocation() {
        Location data = new Location(7, "Fridge", 1);

        Validation<StatusCode, Integer> code = uut.add(data);

        assertTrue(code.isSuccess());

        Validation<StatusCode, List<Location>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().stream().map(f -> f.name).anyMatch(name -> name.equals("Fridge")));
    }

    @Test
    public void renameALocation() {
        Location data = new Location(2, "Cupboard", 0);

        StatusCode result = uut.rename(data, "Basement");

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, List<Location>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().stream().map(f -> f.name).anyMatch(name -> name.equals("Basement")));
    }

    @Test
    public void wrongVersionIsNotRenamed() {
        Location data = new Location(2, "Cupboard", 100);

        StatusCode result = uut.rename(data, "Basement");

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void unknownIsReported() {
        Location data = new Location(100, "Fridge", 1);

        StatusCode result = uut.rename(data, "Cupboard");

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deleteALocation() {
        Location data = new Location(2, "Cupboard", 0);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, List<Location>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertEquals(1, dbData.success().size());
        assertTrue(dbData.success().stream().map(f -> f.name).noneMatch(name -> name.equals("Cupboard")));
    }

    @Test
    public void deleteALocationWithItemsInside() {
        Location data = new Location(1, "Fridge", 0);
        Mockito.when(foodItemHandler.areItemsStoredIn(any(), any())).thenReturn(true);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION, result);
    }

    @Test
    public void invalidDataVersionIsRejected() {
        Location data = new Location(2, "Cupboard", 100);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);

        Validation<StatusCode, List<Location>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertEquals(2, dbData.success().size());
    }

    @Test
    public void unknownDeletionsAreReported() {
        Location data = new Location(100, "Cupboard", 1);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }
}