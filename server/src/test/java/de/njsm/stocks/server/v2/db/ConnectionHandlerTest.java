package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

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
    public void otherExceptionIsForwarded() {
        StatusCode result = uut.runCommand(c -> {
            throw new RuntimeException();
        });

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
    }
}