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

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static de.njsm.stocks.server.v2.db.jooq.tables.Location.LOCATION;
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

        Validation<StatusCode, Stream<Location>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().map(f -> f.name).anyMatch(name -> name.equals("Fridge")));
    }

    @Test
    public void renameALocation() {
        Location data = new Location(2, "Basement", 0);

        StatusCode result = uut.rename(data, data.name);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Location>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().map(f -> f.name).anyMatch(name -> name.equals("Basement")));
    }

    @Test
    public void wrongVersionIsNotRenamed() {
        Location data = new Location(2, "Basement", 100);

        StatusCode result = uut.rename(data, data.name);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void unknownIsReported() {
        Location data = new Location(100, "Cupboard", 1);

        StatusCode result = uut.rename(data, data.name);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deleteALocation() {
        Location data = new Location(2, "Cupboard", 0);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Location>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().map(f -> f.name).noneMatch(name -> name.equals("Cupboard")));
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

        Validation<StatusCode, Stream<Location>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertEquals(2, dbData.success().count());
    }

    @Test
    public void unknownDeletionsAreReported() {
        Location data = new Location(100, "Cupboard", 1);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void historyCleanupCleansDesiredRowsOnly() {
        Period oldData = Period.ofDays(2);
        OffsetDateTime now = OffsetDateTime.now();
        getDSLContext().insertInto(LOCATION)
                .columns(LOCATION.ID,
                        LOCATION.NAME,
                        LOCATION.VALID_TIME_START,
                        LOCATION.VALID_TIME_END,
                        LOCATION.TRANSACTION_TIME_START,
                        LOCATION.TRANSACTION_TIME_END
                )
                .values(3, "", now.minusDays(3), now.minusDays(1), now.minusDays(3), INFINITY)
                .values(3, "", now.minusDays(1), now.plusDays(3), now.minusDays(3), INFINITY)
                .values(3, "", now.plusDays(3), INFINITY, now.minusDays(3), INFINITY)

                .values(3, "", now.minusDays(4), now, now.minusDays(4), now.minusDays(3))
                .values(3, "", now, now.plusDays(3), now.minusDays(4), now.minusDays(3))
                .values(3, "", now.plusDays(3), INFINITY, now.minusDays(4), now.minusDays(3))
                .execute();


        uut.cleanDataOlderThan(oldData);

        long currentRows = uut.get(false, Instant.EPOCH)
                .success()
                .count();
        assertEquals(3, currentRows);

        long allRows = uut.get(true, Instant.EPOCH)
                .success()
                .count();
        assertEquals(5, allRows);
    }
}
