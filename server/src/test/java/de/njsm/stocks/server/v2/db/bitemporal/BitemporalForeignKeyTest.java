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
import de.njsm.stocks.server.v2.db.jooq.tables.records.ScaledUnitRecord;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UnitRecord;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static de.njsm.stocks.server.v2.db.jooq.Tables.CURRENT_USER_DEVICE;
import static de.njsm.stocks.server.v2.db.jooq.Tables.UNIT;
import static de.njsm.stocks.server.v2.db.jooq.tables.ScaledUnit.SCALED_UNIT;
import static org.junit.jupiter.api.Assertions.*;

public class BitemporalForeignKeyTest extends DbTestCase {

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
        ScaledUnitRecord scaledUnit = getScaledUnit(999);
        scaledUnit.setVersion(0);
        scaledUnit.setValidTimeStart(getTimeTick(0));
        scaledUnit.setValidTimeEnd(getTimeTick(2));
        scaledUnit.setTransactionTimeStart(getTimeTick(0));
        scaledUnit.setTransactionTimeEnd(INFINITY);
        scaledUnit.insert();

        assertFailure();
    }

    @Test
    public void foreignKeyBeforeValidityIsRejected() {
        UnitRecord unit = getUnit();
        unit.setVersion(0);
        unit.setValidTimeStart(getTimeTick(1));
        unit.setValidTimeEnd(getTimeTick(3));
        unit.setTransactionTimeStart(getTimeTick(0));
        unit.setTransactionTimeEnd(INFINITY);
        unit.insert();

        ScaledUnitRecord scaledUnit = getScaledUnit(unit.getId());
        scaledUnit.setVersion(0);
        scaledUnit.setValidTimeStart(getTimeTick(0));
        scaledUnit.setValidTimeEnd(getTimeTick(2));
        scaledUnit.setTransactionTimeStart(getTimeTick(0));
        scaledUnit.setTransactionTimeEnd(INFINITY);
        scaledUnit.insert();

        assertFailure();
    }

    @Test
    public void foreignKeyAfterValidityIsRejected() {
        UnitRecord unit = getUnit();
        unit.setVersion(0);
        unit.setValidTimeStart(getTimeTick(0));
        unit.setValidTimeEnd(getTimeTick(2));
        unit.setTransactionTimeStart(getTimeTick(0));
        unit.setTransactionTimeEnd(INFINITY);
        unit.insert();

        ScaledUnitRecord scaledUnit = getScaledUnit(unit.getId());
        scaledUnit.setVersion(0);
        scaledUnit.setValidTimeStart(getTimeTick(1));
        scaledUnit.setValidTimeEnd(getTimeTick(3));
        scaledUnit.setTransactionTimeStart(getTimeTick(0));
        scaledUnit.setTransactionTimeEnd(INFINITY);
        scaledUnit.insert();

        assertFailure();
    }

    @Test
    public void foreignKeyWithValidityInPastTransactionTimeIsRejected() {
        UnitRecord unit = getUnit();
        unit.setVersion(0);
        unit.setValidTimeStart(getTimeTick(0));
        unit.setValidTimeEnd(getTimeTick(3));
        unit.setTransactionTimeStart(getTimeTick(0));
        unit.setTransactionTimeEnd(getTimeTick(3));
        unit.insert();

        ScaledUnitRecord scaledUnit = getScaledUnit(unit.getId());
        scaledUnit.setVersion(0);
        scaledUnit.setValidTimeStart(getTimeTick(1));
        scaledUnit.setValidTimeEnd(getTimeTick(2));
        scaledUnit.setTransactionTimeStart(getTimeTick(0));
        scaledUnit.setTransactionTimeEnd(INFINITY);
        scaledUnit.insert();

        assertFailure();
    }

    @Test
    public void validInsertionWorks() throws SQLException {
        UnitRecord unit = getUnit();
        unit.setVersion(0);
        unit.setValidTimeStart(getTimeTick(0));
        unit.setValidTimeEnd(getTimeTick(3));
        unit.setTransactionTimeStart(getTimeTick(0));
        unit.setTransactionTimeEnd(INFINITY);
        unit.insert();

        ScaledUnitRecord scaledUnit = getScaledUnit(unit.getId());
        scaledUnit.setVersion(0);
        scaledUnit.setValidTimeStart(getTimeTick(1));
        scaledUnit.setValidTimeEnd(getTimeTick(2));
        scaledUnit.setTransactionTimeStart(getTimeTick(0));
        scaledUnit.setTransactionTimeEnd(INFINITY);
        scaledUnit.insert();

        connection.commit();
    }

    private void assertFailure() {
        SQLException e = assertThrows(SQLException.class, () -> connection.commit());
        assertEquals("23514", e.getSQLState());
        assertTrue(e.getMessage().contains("bitemporal_foreign_key"));
    }

    private ScaledUnitRecord getScaledUnit(Integer unitForeignKey) {
        ScaledUnitRecord result = getDSLContext().newRecord(SCALED_UNIT);
        result.setId(ID);
        result.setScale(BigDecimal.ZERO);
        result.setUnit(unitForeignKey);
        result.setInitiates(1);
        return result;
    }

    private UnitRecord getUnit() {
        UnitRecord result = getDSLContext().newRecord(UNIT);
        result.setId(4);
        result.setName("primary key");
        result.setAbbreviation("pk");
        result.setInitiates(1);
        return result;
    }

    private OffsetDateTime getTimeTick(int tick) {
        return anchor.plusDays(tick);
    }
}
