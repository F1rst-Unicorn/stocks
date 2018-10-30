package de.njsm.stocks.server.v2.db;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConnectionFactoryImplTest extends DbTestCase {

    private ConnectionFactoryImpl uut;

    @Before
    public void setup() {
        uut = new ConnectionFactoryImpl(getUrl(),
                getPostgresqlProperties(System.getProperties()));
    }

    @Test
    public void gettingConnectionWorks() throws SQLException {
        Connection con = uut.getConnection();

        assertFalse(con.isClosed());

        uut.returnConnection(con);

        assertTrue(con.isClosed());
    }
}