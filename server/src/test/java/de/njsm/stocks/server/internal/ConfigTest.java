package de.njsm.stocks.server.internal;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static de.njsm.stocks.server.internal.Config.*;
import static org.junit.Assert.assertEquals;

public class ConfigTest {

    private Properties p;

    @Before
    public void setup() {
        p = new Properties();
        p.put(DB_ADDRESS_KEY, "localhost");
        p.put(DB_PORT_KEY, "1234");
        p.put(DB_NAME_KEY, "name");
        p.put(DB_USERNAME_KEY, "username");
        p.put(DB_PASSWORD_KEY, "password");
        p.put(DB_VALIDITY_KEY, "10");
    }

    @Test
    public void testValidInitialisation() {
        Config uut = new Config(p);

        assertEquals("localhost", uut.getDbAddress());
        assertEquals("1234", uut.getDbPort());
        assertEquals("name", uut.getDbName());
        assertEquals("username", uut.getDbUsername());
        assertEquals("password", uut.getDbPassword());
        assertEquals(10, uut.getTicketValidity());
    }

    @Test(expected = NumberFormatException.class)
    public void testInvalidNumberFormat() {
        p.put(DB_VALIDITY_KEY, "invalidNumber");

        new Config(p);
    }
}