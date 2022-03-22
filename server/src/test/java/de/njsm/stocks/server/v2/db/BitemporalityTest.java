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

import de.njsm.stocks.common.api.Location;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.BitemporalLocation;
import de.njsm.stocks.common.api.BitemporalUserDevice;
import de.njsm.stocks.common.api.LocationForDeletion;
import de.njsm.stocks.common.api.LocationForRenaming;
import de.njsm.stocks.server.util.Principals;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BitemporalityTest extends DbTestCase {

    private LocationHandler uut;

    @BeforeEach
    public void setup() {
        FoodItemHandler foodItemHandler = Mockito.mock(FoodItemHandler.class);

        uut = new LocationHandler(getConnectionFactory(), foodItemHandler);
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
        BitemporalUserDevice youngDevice = BitemporalUserDevice.builder()
                .id(7)
                .version(0)
                .validTimeStart(nowAsInstant)
                .validTimeEnd(INFINITY.toInstant())
                .transactionTimeStart(nowAsInstant)
                .transactionTimeEnd(INFINITY.toInstant())
                .initiates(1)
                .name("youngDevice")
                .belongsTo(1)
                .build();
        Principals principals = new Principals("Bob", youngDevice.name(), 1, youngDevice.id());
        uut.setPrincipals(principals);
        LocationForRenaming input = LocationForRenaming.builder()
                .id(1)
                .version(0)
                .name("newName")
                .build();
        getDSLContext().insertInto(USER_DEVICE)
                .columns(USER_DEVICE.ID, USER_DEVICE.VERSION, USER_DEVICE.VALID_TIME_START, USER_DEVICE.VALID_TIME_END, USER_DEVICE.TRANSACTION_TIME_START, USER_DEVICE.TRANSACTION_TIME_END, USER_DEVICE.NAME, USER_DEVICE.BELONGS_TO, USER_DEVICE.INITIATES)
                .values(youngDevice.id(), youngDevice.version(), now, INFINITY, now, INFINITY, youngDevice.name(), youngDevice.belongsTo(), youngDevice.initiates())
                .execute();
        getConnectionFactory().getConnection().commit();

        uut.rename(input);
        getConnectionFactory().getConnection().commit();

        Validation<StatusCode, Stream<Location>> dbData = uut.get(true, Instant.EPOCH);
        assertTrue(dbData.isSuccess());
        assertTrue(dbData.success().map(v -> (BitemporalLocation) v).anyMatch(f -> f.name().equals(input.name())
                && f.id() == input.id()
                && f.version() == input.version() + 1
                && f.validTimeEnd().equals(INFINITY.toInstant())
                && f.transactionTimeEnd().equals(INFINITY.toInstant())
                && f.initiates() == principals.getDid()));
    }

    @Test
    public void youngerDevicesAreAllowedToDeleteOlderLocations() throws SQLException {
        OffsetDateTime now = OffsetDateTime.now();
        Instant nowAsInstant = now.toInstant();
        BitemporalUserDevice youngDevice = BitemporalUserDevice.builder()
                .id(7)
                .version(0)
                .validTimeStart(nowAsInstant)
                .validTimeEnd(INFINITY.toInstant())
                .transactionTimeStart(nowAsInstant)
                .transactionTimeEnd(INFINITY.toInstant())
                .initiates(1)
                .name("youngDevice")
                .belongsTo(1)
                .build();
        Principals principals = new Principals("Bob", youngDevice.name(), 1, youngDevice.id());
        uut.setPrincipals(principals);
        LocationForDeletion input = LocationForDeletion.builder()
                .id(2)
                .version(0)
                .build();
        getDSLContext().insertInto(USER_DEVICE)
                .columns(USER_DEVICE.ID, USER_DEVICE.VERSION, USER_DEVICE.VALID_TIME_START, USER_DEVICE.VALID_TIME_END, USER_DEVICE.TRANSACTION_TIME_START, USER_DEVICE.TRANSACTION_TIME_END, USER_DEVICE.NAME, USER_DEVICE.BELONGS_TO, USER_DEVICE.INITIATES)
                .values(youngDevice.id(), youngDevice.version(), now, INFINITY, now, INFINITY, youngDevice.name(), youngDevice.belongsTo(), youngDevice.initiates())
                .execute();
        getConnectionFactory().getConnection().commit();

        uut.delete(input);
        getConnectionFactory().getConnection().commit();

        Validation<StatusCode, Stream<Location>> dbData = uut.get(true, Instant.EPOCH);
        assertTrue(dbData.isSuccess());
        assertTrue(dbData.success().map(v -> (BitemporalLocation) v).anyMatch(f -> f.name().equals("Cupboard")
                && f.id() == input.id()
                && f.version() == input.version()
                && !f.validTimeEnd().equals(INFINITY.toInstant())
                && f.transactionTimeEnd().equals(INFINITY.toInstant())
                && f.initiates() == principals.getDid()));
    }
}
