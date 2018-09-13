package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.server.v1.internal.db.SqlDatabaseHandler;
import de.njsm.stocks.server.v2.business.StatusCode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BaseSqlDatabaseHandlerTest extends DbTestCase {

    private FailSafeDatabaseHandler uut;

    @Before
    public void setup() {
        uut = new SqlDatabaseHandler(getConnectionFactory(),
                getNewResourceIdentifier());
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
    public void testCloseWithNull() {
        uut.close(null);
    }

    @Test
    public void testClosingWithException() throws SQLException {
        Connection con = Mockito.mock(Connection.class);
        Mockito.doThrow(new SQLException("Mockito")).when(con).close();
        ((MockConnectionFactory) getConnectionFactory()).setThrowException(true);

        uut.close(con);

        ((MockConnectionFactory) getConnectionFactory()).setThrowException(false);
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void commandErrorCodesArePropagated() {
        StatusCode expected = StatusCode.INVALID_DATA_VERSION;

        StatusCode actual = uut.runCommand(context -> expected);

        assertEquals(expected, actual);
    }
}