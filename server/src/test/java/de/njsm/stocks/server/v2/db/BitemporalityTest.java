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
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static de.njsm.stocks.server.v2.db.jooq.Tables.USER_DEVICE;
import static de.njsm.stocks.server.v2.db.jooq.tables.Location.LOCATION;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BitemporalityTest extends DbTestCase {

    private LocationHandler uut;

    @Before
    public void setup() {
        FoodItemHandler foodItemHandler = Mockito.mock(FoodItemHandler.class);

        uut = new LocationHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT,
                foodItemHandler);
        uut.setPrincipals(TEST_USER);
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
        BitemporalUserDevice youngDevice = new BitemporalUserDevice(6, 0, nowAsInstant, INFINITY.toInstant(), nowAsInstant, INFINITY.toInstant(), 1, "youngDevice", 1);
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
        BitemporalUserDevice youngDevice = new BitemporalUserDevice(6, 0, nowAsInstant, INFINITY.toInstant(), nowAsInstant, INFINITY.toInstant(), 1, "youngDevice", 1);
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
}
