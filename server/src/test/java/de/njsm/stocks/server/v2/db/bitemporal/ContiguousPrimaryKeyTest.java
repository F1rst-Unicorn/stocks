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
import de.njsm.stocks.server.v2.db.jooq.tables.records.LocationRecord;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static de.njsm.stocks.server.v2.db.jooq.Tables.CURRENT_USER_DEVICE;
import static de.njsm.stocks.server.v2.db.jooq.Tables.LOCATION;
import static org.junit.jupiter.api.Assertions.*;

public class ContiguousPrimaryKeyTest extends DbTestCase {

    private Connection connection;

    private OffsetDateTime anchor;

    private static final int ID = 3;

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
    public void validInsertionWorks() throws SQLException {
        LocationRecord first = getLocation();
        first.setVersion(0);
        first.setValidTimeStart(getTimeTick(0));
        first.setValidTimeEnd(getTimeTick(1));
        first.setTransactionTimeStart(getTimeTick(0));
        first.setTransactionTimeEnd(INFINITY);
        first.insert();

        LocationRecord second = getLocation();
        second.setVersion(1);
        second.setValidTimeStart(getTimeTick(1));
        second.setValidTimeEnd(INFINITY);
        second.setTransactionTimeStart(getTimeTick(0));
        second.setTransactionTimeEnd(INFINITY);
        second.insert();

        connection.commit();
    }

    @Test
    public void insertionWithGapIsRejected() throws SQLException {
        LocationRecord first = getLocation();
        first.setVersion(0);
        first.setValidTimeStart(getTimeTick(0));
        first.setValidTimeEnd(getTimeTick(1));
        first.setTransactionTimeStart(getTimeTick(0));
        first.setTransactionTimeEnd(INFINITY);
        first.insert();

        LocationRecord second = getLocation();
        second.setVersion(1);
        second.setValidTimeStart(getTimeTick(2));
        second.setValidTimeEnd(INFINITY);
        second.setTransactionTimeStart(getTimeTick(0));
        second.setTransactionTimeEnd(INFINITY);
        second.insert();

        assertFailure();
    }

    @Test
    public void updateToGapIsRejected() throws SQLException {
        LocationRecord first = getLocation();
        first.setVersion(0);
        first.setValidTimeStart(getTimeTick(0));
        first.setValidTimeEnd(getTimeTick(1));
        first.setTransactionTimeStart(getTimeTick(0));
        first.setTransactionTimeEnd(INFINITY);
        first.insert();

        LocationRecord second = getLocation();
        second.setVersion(1);
        second.setValidTimeStart(getTimeTick(1));
        second.setValidTimeEnd(INFINITY);
        second.setTransactionTimeStart(getTimeTick(0));
        second.setTransactionTimeEnd(INFINITY);
        second.insert();

        connection.commit();

        int changed = getDSLContext().update(LOCATION)
                .set(LOCATION.VALID_TIME_START, getTimeTick(2))
                .where(LOCATION.ID.eq(ID))
                .and(LOCATION.VERSION.eq(second.getVersion()))
                .execute();

        assertEquals(1, changed);
        assertFailure();
    }

    @Test
    public void deletingToAGap() throws SQLException {
        LocationRecord first = getLocation();
        first.setVersion(0);
        first.setValidTimeStart(getTimeTick(0));
        first.setValidTimeEnd(getTimeTick(1));
        first.setTransactionTimeStart(getTimeTick(0));
        first.setTransactionTimeEnd(INFINITY);
        first.insert();

        LocationRecord second = getLocation();
        second.setVersion(1);
        second.setValidTimeStart(getTimeTick(1));
        second.setValidTimeEnd(getTimeTick(2));
        second.setTransactionTimeStart(getTimeTick(0));
        second.setTransactionTimeEnd(INFINITY);
        second.insert();

        LocationRecord third = getLocation();
        third.setVersion(2);
        third.setValidTimeStart(getTimeTick(2));
        third.setValidTimeEnd(INFINITY);
        third.setTransactionTimeStart(getTimeTick(0));
        third.setTransactionTimeEnd(INFINITY);
        third.insert();

        connection.commit();

        int changed = getDSLContext().deleteFrom(LOCATION)
                .where(LOCATION.ID.eq(ID))
                .and(LOCATION.VERSION.eq(second.getVersion()))
                .execute();

        assertEquals(1, changed);
        assertFailure();
    }

    private void assertFailure() {
        SQLException e = assertThrows(SQLException.class, () -> connection.commit());
        assertEquals("23514", e.getSQLState());
        assertTrue(e.getMessage().contains("contiguous_primary_key"));
    }

    private LocationRecord getLocation() {
        LocationRecord result = getDSLContext().newRecord(LOCATION);
        result.setId(ID);
        result.setName("name");
        result.setDescription("description");
        result.setInitiates(1);
        return result;
    }

    private OffsetDateTime getTimeTick(int tick) {
        return anchor.plusDays(tick);
    }
}
