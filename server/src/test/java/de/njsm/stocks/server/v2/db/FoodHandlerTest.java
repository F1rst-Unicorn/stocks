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
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.time.Period;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FoodHandlerTest extends DbTestCase {

    private FoodHandler uut;

    @Before
    public void setup() {
        uut = new FoodHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT,
                new InsertVisitor<>());
    }

    @Test
    public void addAFood() {
        Period expirationOffset = Period.ofDays(3);
        Food data = new Food(7, "Banana", 1, false, expirationOffset);

        Validation<StatusCode, Integer> code = uut.add(data);

        assertTrue(code.isSuccess());

        Validation<StatusCode, List<Food>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().stream().map(f -> f.name).anyMatch(name -> name.equals("Banana")));
        assertTrue(dbData.success().stream().map(f -> f.expirationOffset).anyMatch(d -> d.equals(expirationOffset)));
    }

    @Test
    public void editAFood() {
        String newName = "Wine";
        Period expirationOffset = Period.ofDays(3);
        Food data = new Food(2, "Beer", 0, true, Period.ZERO);

        StatusCode result = uut.edit(data, newName, expirationOffset);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, List<Food>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().stream()
                .anyMatch(f -> f.name.equals(newName) && f.toBuy && f.expirationOffset.equals(expirationOffset)));
    }

    @Test
    public void wrongVersionIsNotRenamed() {
        String newName = "Wine";
        Food data = new Food(2, "Beer", 100, true, Period.ZERO);

        StatusCode result = uut.edit(data, newName, Period.ZERO);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void unknownIsReported() {
        String newName = "Wine";
        Food data = new Food(100, "Beer", 1, true, Period.ZERO);

        StatusCode result = uut.edit(data, newName, Period.ZERO);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deleteAFood() {
        Food data = new Food(2, "Beer", 0, true, Period.ZERO);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, List<Food>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertEquals(2, dbData.success().size());
        assertTrue(dbData.success().stream().map(f -> f.name).noneMatch(name -> name.equals("Beer")));
    }

    @Test
    public void invalidDataVersionIsRejected() {
        Food data = new Food(2, "Beer", 100, true, Period.ZERO);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);

        Validation<StatusCode, List<Food>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertEquals(3, dbData.success().size());
    }

    @Test
    public void unknownDeletionsAreReported() {
        Food data = new Food(100, "Beer", 1, true, Period.ZERO);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }
}