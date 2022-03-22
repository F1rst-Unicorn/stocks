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

package de.njsm.stocks.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Properties;

import static de.njsm.stocks.server.Config.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigTest {

    private Properties p;

    @BeforeEach
    public void setup() {
        p = new Properties();
        p.put(DB_ADDRESS_KEY, "localhost");
        p.put(DB_PORT_KEY, "1234");
        p.put(DB_NAME_KEY, "name");
        p.put(DB_VALIDITY_KEY, "10");
        p.put(POSTGRESQL_CONFIG_PREFIX + "user", "user");
        p.put(POSTGRESQL_CONFIG_PREFIX + "password", "password");
        p.put(DB_HISTORY_MAX_PERIOD, "P3Y");
    }

    @Test
    public void testValidInitialisation() {
        Config uut = new Config(p);

        assertEquals("localhost", uut.getDbAddress());
        assertEquals("1234", uut.getDbPort());
        assertEquals("name", uut.getDbName());
        assertEquals("user", uut.getDbProperties().getProperty("user"));
        assertEquals("password", uut.getDbProperties().getProperty("password"));
        assertEquals(10, uut.getTicketValidity());
        assertEquals(Period.ofYears(3), uut.getDbHistoryMaxPeriod());
    }

    @Test
    public void testInvalidNumberInTicketValidity() {
        p.put(DB_VALIDITY_KEY, "invalidNumber");

        assertThrows(NumberFormatException.class, () -> new Config(p));
    }

    @Test
    public void testInvalidPeriodInMaxHistory() {
        p.put(DB_HISTORY_MAX_PERIOD, "invalidPeriod");

        assertThrows(DateTimeParseException.class, () -> new Config(p));
    }
}
