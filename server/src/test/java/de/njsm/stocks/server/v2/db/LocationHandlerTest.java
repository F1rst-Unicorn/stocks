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

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.*;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static de.njsm.stocks.server.v2.db.jooq.Tables.USER_DEVICE;
import static de.njsm.stocks.server.v2.db.jooq.tables.Location.LOCATION;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.Assert.*;
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
                l.getId() == 1 &&
                        l.getVersion() == 0 &&
                        l.getName().equals("Fridge") &&
                        l.getDescription().equals("fridge description") &&
                        l.getInitiates() == 1));

        assertTrue(data.stream().anyMatch(l ->
                l.getId() == 2 &&
                        l.getVersion() == 0 &&
                        l.getName().equals("Cupboard") &&
                        l.getDescription().equals("cupboard description") &&
                        l.getInitiates() == 1));
    }

    @Test
    public void gettingWorks() {

        Validation<StatusCode, Stream<Location>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<Location> data = result.success().collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                l.getId() == 1 &&
                        l.getVersion() == 0 &&
                        l.getName().equals("Fridge") &&
                        l.getDescription().equals("fridge description")));

        assertTrue(data.stream().anyMatch(l ->
                l.getId() == 2 &&
                        l.getVersion() == 0 &&
                        l.getName().equals("Cupboard") &&
                        l.getDescription().equals("cupboard description")));
    }

    @Test
    public void addALocation() {
        LocationForInsertion data = new LocationForInsertion("Fridge");

        Validation<StatusCode, Integer> code = uut.add(data);

        assertTrue(code.isSuccess());

        Validation<StatusCode, Stream<Location>> dbData = uut.get(true, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().map(v -> (BitemporalLocation) v).anyMatch(f ->
                f.getId() == 3 &&
                        f.getVersion() == 0 &&
                        f.getName().equals("Fridge") &&
                        f.getInitiates() == TEST_USER.getDid()));
    }

    @Test
    public void renameALocation() {
        LocationForRenaming data = new LocationForRenaming(2, 0, "Basement");

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Location>> dbData = uut.get(true, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().map(v -> (BitemporalLocation) v).anyMatch(f ->
                f.getName().equals("Basement")
                        && f.getId() == 2
                        && f.getVersion() == 1
                        && f.getInitiates() == TEST_USER.getDid()));
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
    public void deleteALocation() {
        LocationForDeletion data = new LocationForDeletion(2, 0);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Location>> dbData = uut.get(false, Instant.EPOCH);
        assertTrue(dbData.isSuccess());
        assertTrue(dbData.success().map(Location::getName).noneMatch(name -> name.equals("Cupboard")));

        dbData = uut.get(true, Instant.EPOCH);
        assertTrue(dbData.isSuccess());
        assertTrue(dbData.success().map(v -> (BitemporalLocation) v).anyMatch(f -> f.getName().equals("Cupboard")
                && f.getId() == 2
                && f.getVersion() == 0
                && !f.getValidTimeEnd().equals(INFINITY.toInstant())
                && f.getTransactionTimeEnd().equals(INFINITY.toInstant())
                && f.getInitiates() == TEST_USER.getDid()));
    }

    @Test
    public void deleteALocationWithItemsInsideFails() {
        LocationForDeletion data = new LocationForDeletion(1, 0);
        Mockito.when(foodItemHandler.areItemsStoredIn(any(), any())).thenReturn(true);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION, result);
    }

    @Test
    public void invalidDataVersionIsRejected() {
        LocationForDeletion data = new LocationForDeletion(2, 100);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);

        Validation<StatusCode, Stream<Location>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertEquals(2, dbData.success().count());
    }

    @Test
    public void unknownDeletionsAreReported() {
        LocationForDeletion data = new LocationForDeletion(100, 0);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<Location>> result = uut.get(true, Instant.EPOCH);

        BitemporalLocation sample = (BitemporalLocation) result.success().findAny().get();
        assertNotNull(sample.getValidTimeStart());
        assertNotNull(sample.getValidTimeEnd());
        assertNotNull(sample.getTransactionTimeStart());
        assertNotNull(sample.getTransactionTimeEnd());
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
                        LOCATION.TRANSACTION_TIME_END,
                        LOCATION.INITIATES
                )
                .values(3, "", now.minusDays(3), now.minusDays(1), now.minusDays(3), INFINITY, 1)
                .values(3, "", now.minusDays(1), now.plusDays(3), now.minusDays(3), INFINITY, 1)
                .values(3, "", now.plusDays(3), INFINITY, now.minusDays(3), INFINITY, 1)

                .values(3, "", now.minusDays(4), now, now.minusDays(4), now.minusDays(3), 1)
                .values(3, "", now, now.plusDays(3), now.minusDays(4), now.minusDays(3), 1)
                .values(3, "", now.plusDays(3), INFINITY, now.minusDays(4), now.minusDays(3), 1)
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

    @Test
    public void gettingLatestChangesWorks() {
        OffsetDateTime now = OffsetDateTime.now();
        getDSLContext().insertInto(LOCATION)
                .columns(LOCATION.ID,
                        LOCATION.NAME,
                        LOCATION.VALID_TIME_START,
                        LOCATION.VALID_TIME_END,
                        LOCATION.TRANSACTION_TIME_START,
                        LOCATION.TRANSACTION_TIME_END,
                        LOCATION.INITIATES
                )

                // currently valid record, will be reported
                .values(3, "", now.minusDays(3), now.minusDays(1), now.minusDays(3), INFINITY, 1)
                .values(3, "", now.minusDays(1), now.plusDays(3), now.minusDays(3), INFINITY, 1)
                .values(3, "", now.plusDays(3), INFINITY, now.minusDays(3), INFINITY, 1)

                // "just terminated" record, will be reported
                .values(3, "", now.minusDays(4), now, now.minusDays(4), now.minusDays(3), 1)
                .values(3, "", now, now.plusDays(3), now.minusDays(4), now.minusDays(3), 1)
                .values(3, "", now.plusDays(3), INFINITY, now.minusDays(4), now.minusDays(3), 1)

                // "very old" record, not reported
                .values(3, "", now.minusDays(5), now, now.minusDays(5), now.minusDays(4), 1)
                .values(3, "", now, now.plusDays(4), now.minusDays(5), now.minusDays(4), 1)
                .values(3, "", now.plusDays(4), INFINITY, now.minusDays(5), now.minusDays(4), 1)
                .execute();


        //                                             DB only tracks at microsecond precision -----v
        Validation<StatusCode, Stream<Location>> result = uut.get(true, now.minusDays(3).minusNanos(1000).toInstant());

        long retrievedRows = result
                .success()
                .count();
        assertEquals(8, retrievedRows);
    }

    @Test
    public void youngerDevicesAreAllowedToChangeOlderLocations() throws SQLException {
        OffsetDateTime now = OffsetDateTime.now();
        Instant nowAsInstant = now.toInstant();
        BitemporalUserDevice youngDevice = new BitemporalUserDevice(5, 0, nowAsInstant, INFINITY.toInstant(), nowAsInstant, INFINITY.toInstant(), 1, "youngDevice", 1);
        Principals principals = new Principals("Bob", youngDevice.getName(), 1, youngDevice.getId());
        uut.setPrincipals(principals);
        LocationForRenaming input = new LocationForRenaming(1, 0, "newName");
        getDSLContext().insertInto(USER_DEVICE)
                .columns(USER_DEVICE.ID, USER_DEVICE.VERSION, USER_DEVICE.VALID_TIME_START, USER_DEVICE.VALID_TIME_END, USER_DEVICE.TRANSACTION_TIME_START, USER_DEVICE.TRANSACTION_TIME_END, USER_DEVICE.NAME, USER_DEVICE.BELONGS_TO, USER_DEVICE.INITIATES)
                .values(youngDevice.getId(), youngDevice.getVersion(), now, INFINITY, now, INFINITY, youngDevice.getName(), youngDevice.getBelongsTo(), youngDevice.getInitiates())
                .execute();
        getConnectionFactory().getConnection().commit();

        uut.rename(input);
        getConnectionFactory().getConnection().commit();

        Validation<StatusCode, Stream<Location>> dbData = uut.get(true, Instant.EPOCH);
        assertTrue(dbData.isSuccess());
        assertTrue(dbData.success().map(v -> (BitemporalLocation) v).anyMatch(f -> f.getName().equals(input.getNewName())
                && f.getId() == input.getId()
                && f.getVersion() == input.getVersion() + 1
                && f.getValidTimeEnd().equals(INFINITY.toInstant())
                && f.getTransactionTimeEnd().equals(INFINITY.toInstant())
                && f.getInitiates() == principals.getDid()));
    }

    @Test
    public void youngerDevicesAreAllowedToDeleteOlderLocations() throws SQLException {
        OffsetDateTime now = OffsetDateTime.now();
        Instant nowAsInstant = now.toInstant();
        BitemporalUserDevice youngDevice = new BitemporalUserDevice(5, 0, nowAsInstant, INFINITY.toInstant(), nowAsInstant, INFINITY.toInstant(), 1, "youngDevice", 1);
        Principals principals = new Principals("Bob", youngDevice.getName(), 1, youngDevice.getId());
        uut.setPrincipals(principals);
        LocationForDeletion input = new LocationForDeletion(2, 0);
        getDSLContext().insertInto(USER_DEVICE)
                .columns(USER_DEVICE.ID, USER_DEVICE.VERSION, USER_DEVICE.VALID_TIME_START, USER_DEVICE.VALID_TIME_END, USER_DEVICE.TRANSACTION_TIME_START, USER_DEVICE.TRANSACTION_TIME_END, USER_DEVICE.NAME, USER_DEVICE.BELONGS_TO, USER_DEVICE.INITIATES)
                .values(youngDevice.getId(), youngDevice.getVersion(), now, INFINITY, now, INFINITY, youngDevice.getName(), youngDevice.getBelongsTo(), youngDevice.getInitiates())
                .execute();
        getConnectionFactory().getConnection().commit();

        uut.delete(input);
        getConnectionFactory().getConnection().commit();

        Validation<StatusCode, Stream<Location>> dbData = uut.get(true, Instant.EPOCH);
        assertTrue(dbData.isSuccess());
        assertTrue(dbData.success().map(v -> (BitemporalLocation) v).anyMatch(f -> f.getName().equals("Cupboard")
                && f.getId() == input.getId()
                && f.getVersion() == input.getVersion()
                && !f.getValidTimeEnd().equals(INFINITY.toInstant())
                && f.getTransactionTimeEnd().equals(INFINITY.toInstant())
                && f.getInitiates() == principals.getDid()));
    }

    @Test
    public void settingDescriptionWorks() {
        LocationForSetDescription data = new LocationForSetDescription(1, 0, "new description");

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
}
