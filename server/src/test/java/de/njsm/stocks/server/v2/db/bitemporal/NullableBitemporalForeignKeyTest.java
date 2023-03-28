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
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;
import de.njsm.stocks.server.v2.db.jooq.tables.records.LocationRecord;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static de.njsm.stocks.server.v2.db.jooq.Tables.*;
import static org.junit.jupiter.api.Assertions.*;

public class NullableBitemporalForeignKeyTest extends DbTestCase {

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
    public void insertingNullIsValid() throws SQLException {
        FoodRecord food = getFood(null);
        food.setVersion(0);
        food.setValidTimeStart(getTimeTick(0));
        food.setValidTimeEnd(getTimeTick(2));
        food.setTransactionTimeStart(getTimeTick(0));
        food.setTransactionTimeEnd(INFINITY);
        food.insert();

        connection.commit();
    }

    @Test
    public void nonExistentForeignKeyIsRejected() {
        FoodRecord scaledUnit = getFood(999);
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
        LocationRecord location = getLocation();
        location.setVersion(0);
        location.setValidTimeStart(getTimeTick(1));
        location.setValidTimeEnd(getTimeTick(3));
        location.setTransactionTimeStart(getTimeTick(0));
        location.setTransactionTimeEnd(INFINITY);
        location.insert();

        FoodRecord food = getFood(location.getId());
        food.setVersion(0);
        food.setValidTimeStart(getTimeTick(0));
        food.setValidTimeEnd(getTimeTick(2));
        food.setTransactionTimeStart(getTimeTick(0));
        food.setTransactionTimeEnd(INFINITY);
        food.insert();

        assertFailure();
    }

    @Test
    public void foreignKeyAfterValidityIsRejected() {
        LocationRecord location = getLocation();
        location.setVersion(0);
        location.setValidTimeStart(getTimeTick(0));
        location.setValidTimeEnd(getTimeTick(2));
        location.setTransactionTimeStart(getTimeTick(0));
        location.setTransactionTimeEnd(INFINITY);
        location.insert();

        FoodRecord food = getFood(location.getId());
        food.setVersion(0);
        food.setValidTimeStart(getTimeTick(1));
        food.setValidTimeEnd(getTimeTick(3));
        food.setTransactionTimeStart(getTimeTick(0));
        food.setTransactionTimeEnd(INFINITY);
        food.insert();

        assertFailure();
    }

    @Test
    public void foreignKeyWithValidityInPastTransactionTimeIsRejected() {
        LocationRecord location = getLocation();
        location.setVersion(0);
        location.setValidTimeStart(getTimeTick(0));
        location.setValidTimeEnd(getTimeTick(3));
        location.setTransactionTimeStart(getTimeTick(0));
        location.setTransactionTimeEnd(getTimeTick(3));
        location.insert();

        FoodRecord food = getFood(location.getId());
        food.setVersion(0);
        food.setValidTimeStart(getTimeTick(1));
        food.setValidTimeEnd(getTimeTick(2));
        food.setTransactionTimeStart(getTimeTick(0));
        food.setTransactionTimeEnd(INFINITY);
        food.insert();

        assertFailure();
    }

    private void assertFailure() {
        SQLException e = assertThrows(SQLException.class, () -> connection.commit());
        assertEquals("23514", e.getSQLState());
        assertTrue(e.getMessage().contains("bitemporal_foreign_key"));
    }

    private FoodRecord getFood(Integer locationForeignKey) {
        FoodRecord result = getDSLContext().newRecord(FOOD);
        result.setId(ID);
        result.setName("food");
        result.setLocation(locationForeignKey);
        result.setStoreUnit(1);
        result.setInitiates(1);
        return result;
    }

    private LocationRecord getLocation() {
        LocationRecord result = getDSLContext().newRecord(LOCATION);
        result.setId(4);
        result.setName("primary key");
        result.setInitiates(1);
        return result;
    }

    private OffsetDateTime getTimeTick(int tick) {
        return anchor.plusDays(tick);
    }
}
