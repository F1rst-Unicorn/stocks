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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.web.security.HeaderAuthenticatorTest;
import de.njsm.stocks.server.v2.web.security.StocksAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static de.njsm.stocks.server.v2.db.jooq.tables.Location.LOCATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProlongValidTimeTest extends DbTestCase {

    private LocationHandler uut;

    @BeforeEach
    void setUp() {
        FoodItemHandler foodItemHandler = Mockito.mock(FoodItemHandler.class);

        uut = new LocationHandler(getConnectionFactory(), foodItemHandler);
        SecurityContextHolder.getContext().setAuthentication(new StocksAuthentication(HeaderAuthenticatorTest.TEST_USER));
    }

    @Test
    public void prolongingWorks() throws SQLException {
        int id = 1;
        StatusCode update = uut.currentUpdate(
                List.of(
                        LOCATION.NAME.concat(" updated"),
                        LOCATION.DESCRIPTION
                ),
                LOCATION.ID.eq(id)
        );
        assertTrue(update.isSuccess());
        getConnectionFactory().getConnection().commit();
        var actualNumberOfTimeRectangles = getDSLContext().selectCount()
                .from(LOCATION)
                .where(LOCATION.ID.eq(id))
                .fetchSingle(0, Integer.class);
        assertEquals(3, actualNumberOfTimeRectangles);

        uut.prolongValidTimeStart(
                getDSLContext(),
                uut.tableDescription(),
                OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()),
                id);

        actualNumberOfTimeRectangles = getDSLContext().selectCount()
                .from(LOCATION)
                .where(LOCATION.ID.eq(id))
                .fetchSingle(0, Integer.class);
        assertEquals(4, actualNumberOfTimeRectangles);
    }
}
