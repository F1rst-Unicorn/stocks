package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

public class ConnectionHandlerTest extends DbTestCase {

    private ConnectionHandler uut;

    @Before
    public void setup() {
        uut = new ConnectionHandler(getNewResourceIdentifier(),
                getConnectionFactory(),
                CIRCUIT_BREAKER_TIMEOUT);
    }

    @Test
    public void defaultErrorCodeIsCorrect() {
        assertEquals(StatusCode.DATABASE_UNREACHABLE, uut.getDefaultErrorCode());
    }

    @Test
    public void otherSqlExceptionIsForwarded() {
        StatusCode result = uut.runCommand(c -> {
            throw new SQLException();
        });

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
    }

    @Test
    public void nestedSqlExceptionIsForwarded() {
        StatusCode result = uut.runCommand(c -> {
            throw new RuntimeException("", new SQLException("", "40001", null));
        });

        assertEquals(StatusCode.SERIALISATION_CONFLICT, result);
    }

    @Test
    public void otherExceptionIsForwarded() {
        StatusCode result = uut.runCommand(c -> {
            throw new RuntimeException();
        });

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
    }

    @Test
    public void sqlExceptionWithDifferentCodeIsIgnored() {
        assertFalse(ConnectionHandler.isSerialisationConflict(new SQLException("", "40002", null)));
    }

    @Test
    public void serialisationExceptionIsTransformed() {
        RuntimeException e = new RuntimeException("", new SQLException("", "40001", null));

        Validation<StatusCode, Object> result = ConnectionHandler.lookForSqlException(e);

        assertEquals(StatusCode.SERIALISATION_CONFLICT, result.fail());
    }

    @Test(expected = RuntimeException.class)
    public void otherExceptionIsThrown() {
        RuntimeException e = new RuntimeException("", new SQLException("", "40002", null));

        ConnectionHandler.lookForSqlException(e);
    }
}