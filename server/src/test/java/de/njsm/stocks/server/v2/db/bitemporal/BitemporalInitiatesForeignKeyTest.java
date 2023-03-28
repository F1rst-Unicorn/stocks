/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.server.v2.db.bitemporal;

import de.njsm.stocks.server.v2.db.DbTestCase;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserDeviceRecord;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserRecord;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static de.njsm.stocks.server.v2.db.jooq.Tables.*;
import static org.junit.jupiter.api.Assertions.*;

public class BitemporalInitiatesForeignKeyTest extends DbTestCase {

    private Connection connection;

    private OffsetDateTime anchor;

    private static final int ID = 6;

    @BeforeEach
    void setUp() throws SQLException {
        connection = getConnectionFactory().getConnection();
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        anchor = getDSLContext().select(DSL.min(CURRENT_USER_DEVICE.VALID_TIME_START))
                .from(CURRENT_USER_DEVICE)
                .fetchOne()
                .value1();
    }

    @Test
    public void nonExistentForeignKeyIsRejected() {
        UserRecord user = getUser(999);
        user.setVersion(0);
        user.setValidTimeStart(getTimeTick(0));
        user.setValidTimeEnd(getTimeTick(2));
        user.setTransactionTimeStart(getTimeTick(0));
        user.setTransactionTimeEnd(INFINITY);
        user.insert();

        assertFailure();
    }

    @Test
    public void userInitiatedByDeviceBeforeItsExistenceInRejected() {
        UserRecord user = getUser(1);
        user.setVersion(0);
        user.setValidTimeStart(getTimeTick(0));
        user.setValidTimeEnd(getTimeTick(2));
        user.setTransactionTimeStart(getTimeTick(-1));
        user.setTransactionTimeEnd(INFINITY);
        user.insert();

        assertFailure();
    }

    @Test
    public void userInitiatedByDeviceAfterItsExistenceInRejected() {
        UserDeviceRecord device = getDSLContext().newRecord(USER_DEVICE);
        device.setName("terminated");
        device.setBelongsTo(2);
        device.setValidTimeStart(getTimeTick(0));
        device.setValidTimeEnd(getTimeTick(3));
        device.setTransactionTimeStart(getTimeTick(0));
        device.setTransactionTimeEnd(getTimeTick(3));
        device.setInitiates(1);
        device.setVersion(0);
        device.insert();

        UserRecord user = getUser(device.getId());
        user.setVersion(0);
        user.setValidTimeStart(getTimeTick(0));
        user.setValidTimeEnd(getTimeTick(2));
        user.setTransactionTimeStart(getTimeTick(1));
        user.setTransactionTimeEnd(INFINITY);
        user.insert();

        assertFailure();
    }

    private void assertFailure() {
        SQLException e = assertThrows(SQLException.class, () -> connection.commit());
        assertEquals("23514", e.getSQLState());
        assertTrue(e.getMessage().contains("bitemporal_initiates_foreign_key"));
    }

    private UserRecord getUser(Integer initiates) {
        UserRecord result = getDSLContext().newRecord(USER);
        result.setId(ID);
        result.setName("name");
        result.setInitiates(initiates);
        return result;
    }

    private OffsetDateTime getTimeTick(int tick) {
        return anchor.plusDays(tick);
    }
}
