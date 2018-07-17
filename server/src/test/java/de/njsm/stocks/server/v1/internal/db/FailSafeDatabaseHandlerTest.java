package de.njsm.stocks.server.v1.internal.db;

import de.njsm.stocks.common.util.ConsumerWithExceptions;
import de.njsm.stocks.server.Config;
import de.njsm.stocks.server.v2.db.FailSafeDatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class FailSafeDatabaseHandlerTest {

    private FailSafeDatabaseHandler uut;

    @Before
    public void setup() throws Exception {
        Config c = new Config(System.getProperties());

        uut = new FailSafeDatabaseHandler(String.format("jdbc:mariadb://%s:%s/%s?useLegacyDatetimeCode=false&serverTimezone=+00:00",
                c.getDbAddress(), c.getDbPort(), c.getDbName()),
                c.getDbUsername(),
                c.getDbPassword(),
                "test");
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
}