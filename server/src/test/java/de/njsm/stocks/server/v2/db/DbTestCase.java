package de.njsm.stocks.server.v2.db;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public abstract class DbTestCase {

    private static int resourceCounter = 0;

    private static Connection connection;

    @Before
    public void resetDatabase() throws SQLException {
        connection = createConnection();
        SampleData.insertSampleData(connection);

        resourceCounter++;
    }

    @After
    public void tearDown() throws SQLException {
        connection.close();
    }

    protected Connection getConnection() {
        return connection;
    }

    protected DSLContext getDSLContext() {
        return DSL.using(connection, SQLDialect.POSTGRES_10);
    }

    protected String getNewResourceIdentifier() {
        return "hystrix group " + String.valueOf(resourceCounter);
    }

    static Connection createConnection() throws SQLException {
        String url = getUrl();

        return DriverManager.getConnection(url, getPostgresqlProperties(System.getProperties()));
    }

    protected static String getUrl() {
        String address = System.getProperty("de.njsm.stocks.server.v2.db.host");
        String port = System.getProperty("de.njsm.stocks.server.v2.db.port");
        String name = System.getProperty("de.njsm.stocks.server.v2.db.name");

        return String.format("jdbc:postgresql://%s:%s/%s",
                address,
                port,
                name);
    }

    protected static Properties getPostgresqlProperties(Properties config) {
        String postgresqlConfigPrefix = "de.njsm.stocks.server.v2.db.postgres.";
        Properties result = new Properties();

        for (Map.Entry<Object, Object> entry: config.entrySet()) {
            if (entry.getKey() instanceof String &&
                entry.getValue() instanceof String) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (key.startsWith(postgresqlConfigPrefix) &&
                        ! value.isEmpty()) {
                    result.put(
                            key.replace(postgresqlConfigPrefix, ""),
                            entry.getValue()
                    );
                }
            }
        }
        return result;
    }


}
