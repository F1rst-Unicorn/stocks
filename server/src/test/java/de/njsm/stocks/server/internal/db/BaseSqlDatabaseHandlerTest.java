package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.server.internal.Config;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertNull;

public class BaseSqlDatabaseHandlerTest {

    private SqlDatabaseHandler uut;

    @Before
    public void resetDatabase() throws SQLException {

        Config c = new Config(System.getProperties());
        uut = new SqlDatabaseHandler(String.format("jdbc:mariadb://%s:%s/%s?useLegacyDatetimeCode=false&serverTimezone=+00:00",
                c.getDbAddress(), c.getDbPort(), c.getDbName()),
                c.getDbUsername(),
                c.getDbPassword());
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


}