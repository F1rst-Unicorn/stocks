package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.server.Config;
import de.njsm.stocks.server.v1.internal.db.SqlDatabaseHandler;
import de.njsm.stocks.server.v2.business.StatusCode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BaseSqlDatabaseHandlerTest {

    private FailSafeDatabaseHandler uut;

    @Before
    public void resetDatabase() {

        Config c = new Config(System.getProperties());
        uut = new SqlDatabaseHandler(String.format("jdbc:mariadb://%s:%s/%s?useLegacyDatetimeCode=false&serverTimezone=+00:00",
                c.getDbAddress(), c.getDbPort(), c.getDbName()),
                c.getDbUsername(),
                c.getDbPassword(),
                "base");
    }


    @Test
    public void exceptionReturnsNull() {
        Object result = uut.runSqlOperation((FunctionWithExceptions<Connection, Object, SQLException>) con -> {
            throw new SQLException("test");
        });

        assertNull(result);
    }

    @Test
    public void testRollbackWithNull() {
        uut.rollback(null);
    }

    @Test
    public void testRollbackWithConnection() throws SQLException {
        Connection con = Mockito.mock(Connection.class);

        uut.rollback(con);

        Mockito.verify(con).rollback();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testRollbackWithException() throws SQLException {
        Connection con = Mockito.mock(Connection.class);
        Mockito.doThrow(new SQLException("Mockito")).when(con).rollback();

        uut.rollback(con);

        Mockito.verify(con).rollback();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testClosingWithException() throws SQLException {
        Connection con = Mockito.mock(Connection.class);
        Mockito.doThrow(new SQLException("Mockito")).when(con).close();

        uut.close(con);

        Mockito.verify(con).close();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void commandErrorCodesArePropagated() {
        StatusCode expected = StatusCode.INVALID_DATA_VERSION;

        StatusCode actual = uut.runCommand(context -> expected);

        assertEquals(expected, actual);
    }
}