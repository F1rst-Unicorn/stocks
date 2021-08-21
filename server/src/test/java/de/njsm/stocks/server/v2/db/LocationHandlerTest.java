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
import de.njsm.stocks.server.v2.db.jooq.tables.records.LocationRecord;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;

public class LocationHandlerTest extends DbTestCase implements CrudOperationsTest<LocationRecord, Location> {

    private LocationHandler uut;

    private FoodItemHandler foodItemHandler;

    @BeforeEach
    public void setup() {
        foodItemHandler = Mockito.mock(FoodItemHandler.class);

        uut = new LocationHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT,
                foodItemHandler);
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void gettingBitemporalWorks() {

        Validation<StatusCode, Stream<Location>> result = uut.get(true, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<BitemporalLocation> data = result.success()
                .map(v -> (BitemporalLocation) v).collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                l.id() == 1 &&
                        l.version() == 0 &&
                        l.name().equals("Fridge") &&
                        l.description().equals("fridge description") &&
                        l.initiates() == 1));

        assertTrue(data.stream().anyMatch(l ->
                l.id() == 2 &&
                        l.version() == 0 &&
                        l.name().equals("Cupboard") &&
                        l.description().equals("cupboard description") &&
                        l.initiates() == 1));
    }

    @Test
    public void gettingWorks() {

        Validation<StatusCode, Stream<Location>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<Location> data = result.success().collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                l.id() == 1 &&
                        l.version() == 0 &&
                        l.name().equals("Fridge") &&
                        l.description().equals("fridge description")));

        assertTrue(data.stream().anyMatch(l ->
                l.id() == 2 &&
                        l.version() == 0 &&
                        l.name().equals("Cupboard") &&
                        l.description().equals("cupboard description")));
    }

    @Override
    public LocationForInsertion getInsertable() {
        return new LocationForInsertion("Fridge");
    }

    @Test
    public void renameALocation() {
        LocationForRenaming data = new LocationForRenaming(2, 0, "Basement");

        StatusCode result = uut.rename(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void wrongVersionIsNotRenamed() {
        LocationForRenaming data = new LocationForRenaming(2, 100, "Basement");

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void unknownIsReported() {
        LocationForRenaming data = new LocationForRenaming(100, 1, "Basement");

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deleteALocationWithItemsInsideFails() {
        LocationForDeletion data = new LocationForDeletion(1, 0);
        Mockito.when(foodItemHandler.areItemsStoredIn(any(), any())).thenReturn(true);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION, result);
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<Location>> result = uut.get(true, Instant.EPOCH);

        BitemporalLocation sample = (BitemporalLocation) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Test
    public void settingDescriptionWorks() {
        LocationForSetDescription data = new LocationForSetDescription(1, 0, "new description");

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
    public void settingDescriptionOnAbsentLocationIsReported() {
        LocationForSetDescription data = new LocationForSetDescription(-1, 0, "new description");

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void settingDescriptionOnInvalidVersionIsReported() {
        LocationForSetDescription data = new LocationForSetDescription(1, 1, "new description");

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void settingDescriptionWithoutChangeIsPrevented() {
        LocationForSetDescription data = new LocationForSetDescription(1, 0, "fridge description");

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Override
    public CrudDatabaseHandler<LocationRecord, Location> getDbHandler() {
        return uut;
    }

    @Override
    public int getNumberOfEntities() {
        return 2;
    }

    @Override
    public LocationForDeletion getUnknownEntity() {
        return new LocationForDeletion(getNumberOfEntities() + 1, getValidEntity().version() + 1);
    }

    @Override
    public LocationForDeletion getWrongVersionEntity() {
        return new LocationForDeletion(getValidEntity().id(), getValidEntity().version() + 1);
    }

    @Override
    public LocationForDeletion getValidEntity() {
        return new LocationForDeletion(2, 0);
    }
}
