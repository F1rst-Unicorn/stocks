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
import de.njsm.stocks.server.v2.db.jooq.tables.records.TicketRecord;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserDeviceRecord;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static de.njsm.stocks.server.v2.db.jooq.Tables.*;
import static org.junit.jupiter.api.Assertions.*;

public class CurrentToBitemporalForeignKeyTest extends DbTestCase {

    private Connection connection;

    private OffsetDateTime anchor;

    private static final int ID = 4;

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
        TicketRecord ticket = getTicket(999);
        ticket.insert();

        assertFailure();
    }

    @Test
    public void foreignKeyWithTerminatedValidTime() {
        UserDeviceRecord userDevice = getUserDevice();
        userDevice.setVersion(0);
        userDevice.setValidTimeStart(getTimeTick(1));
        userDevice.setValidTimeEnd(getTimeTick(3));
        userDevice.setTransactionTimeStart(getTimeTick(0));
        userDevice.setTransactionTimeEnd(INFINITY);
        userDevice.insert();

        TicketRecord ticket = getTicket(userDevice.getId());
        ticket.insert();

        assertFailure();
    }

    @Test
    public void foreignKeyWithTerminatedTransactionTime() {
        UserDeviceRecord userDevice = getUserDevice();
        userDevice.setVersion(0);
        userDevice.setValidTimeStart(getTimeTick(1));
        userDevice.setValidTimeEnd(INFINITY);
        userDevice.setTransactionTimeStart(getTimeTick(0));
        userDevice.setTransactionTimeEnd(getTimeTick(3));
        userDevice.insert();

        TicketRecord ticket = getTicket(userDevice.getId());
        ticket.insert();

        assertFailure();
    }

    @Test
    public void validInsertionWorks() throws SQLException {
        UserDeviceRecord userDevice = getUserDevice();
        userDevice.setVersion(0);
        userDevice.setValidTimeStart(getTimeTick(0));
        userDevice.setValidTimeEnd(INFINITY);
        userDevice.setTransactionTimeStart(getTimeTick(0));
        userDevice.setTransactionTimeEnd(INFINITY);
        userDevice.insert();

        TicketRecord ticket = getTicket(userDevice.getId());
        ticket.insert();

        connection.commit();
    }

    private void assertFailure() {
        SQLException e = assertThrows(SQLException.class, () -> connection.commit());
        assertEquals("23514", e.getSQLState());
        assertTrue(e.getMessage().contains("bitemporal_foreign_key"));
    }

    private TicketRecord getTicket(Integer userDeviceForeignKey) {
        TicketRecord result = getDSLContext().newRecord(TICKET);
        result.setTicket("0000");
        result.setBelongsDevice(userDeviceForeignKey);
        return result;
    }

    private UserDeviceRecord getUserDevice() {
        UserDeviceRecord result = getDSLContext().newRecord(USER_DEVICE);
        result.setName("primary key");
        result.setBelongsTo(1);
        result.setInitiates(1);
        return result;
    }

    private OffsetDateTime getTimeTick(int tick) {
        return anchor.plusDays(tick);
    }
}
