package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.util.ConsumerWithExceptions;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class FailSafeDatabaseHandlerTest extends DbTestCase {

    private FailSafeDatabaseHandler uut;

    @Before
    public void setup() throws SQLException {
        Connection c = getConnection();
        c.setAutoCommit(false);
        uut = new FailSafeDatabaseHandler(getConnection(),
                getNewResourceIdentifier());
    }

    @Test
    public void openCircuitBreaker() throws InterruptedException {
        ConsumerWithExceptions<Connection, SQLException> input = (con) -> {
            throw new SQLException("test");
        };

        uut.runSqlOperation(input);
        Thread.sleep(500);      // hystrix window has to shift

        Assert.assertTrue(uut.isCircuitBreakerOpen());
    }

    @Test
    public void openCircuitBreakerWithUncheckedException() throws InterruptedException {
        ConsumerWithExceptions<Connection, SQLException> input = (con) -> {
            throw new RuntimeException("test");
        };

        uut.runSqlOperation(input);
        Thread.sleep(500);      // hystrix window has to shift

        Assert.assertTrue(uut.isCircuitBreakerOpen());
    }

    @Test
    public void openCircuitBreakerInNewApi() throws InterruptedException {
        FunctionWithExceptions<DSLContext, Validation<StatusCode, String>, SQLException> input = (con) -> {
            throw new DataAccessException("test");
        };

        uut.runFunction(input);
        Thread.sleep(500);      // hystrix window has to shift

        Assert.assertTrue(uut.isCircuitBreakerOpen());
    }

    @Test
    public void testCommitting() throws SQLException {

        StatusCode result = uut.commit();

        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(getConnection().isClosed());
    }

    @Test
    public void testRollingBack() throws SQLException {

        StatusCode result = uut.rollback();

        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(getConnection().isClosed());
    }

    @Test
    public void serialisationErrorIsNoted() throws SQLException {
        Connection concurrentConnection = DbTestCase.createConnection();
        concurrentConnection.setAutoCommit(false);
        concurrentConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        Statement statement = concurrentConnection.createStatement();
        statement.execute("create table concurrency_test (class int not null, value int not null);" +
                "insert into concurrency_test (class, value) values (1, 10);" +
                "insert into concurrency_test (class, value) values (1, 20);" +
                "insert into concurrency_test (class, value) values (2, 100);" +
                "insert into concurrency_test (class, value) values (2, 200);");
        concurrentConnection.commit();

        StatusCode commandCode = uut.runCommand(context -> {
           context.execute("select sum(value) from concurrency_test where class = 1");
           context.execute("insert into concurrency_test (class, value) values (2, 30)");
           return StatusCode.SUCCESS;
        });

        statement.execute("select sum(value) from concurrency_test where class = 2");
        statement.execute("insert into concurrency_test (class, value) values (1, 300)");
        concurrentConnection.commit();

        StatusCode commitStatusCode = uut.commit();

        statement.execute("drop table concurrency_test");
        concurrentConnection.commit();
        concurrentConnection.close();

        assertEquals(StatusCode.SUCCESS, commandCode);
        assertEquals(StatusCode.SERIALISATION_CONFLICT, commitStatusCode);
    }
}