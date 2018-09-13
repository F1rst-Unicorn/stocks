package de.njsm.stocks.server.v2.db;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DbTestCase {

    private static int resourceCounter = 0;

    private static Connection connection;

    private ConnectionFactory factory;

    @BeforeClass
    public static void connect() throws SQLException {
        connection = getConnection();
    }

    @Before
    public void resetDatabase() throws SQLException {
        SampleData.insertSampleData(connection);
        factory = new MockConnectionFactory(connection);

        resourceCounter++;
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        connection.close();
    }

    protected ConnectionFactory getConnectionFactory() {
        return factory;
    }

    protected String getNewResourceIdentifier() {
        return "hystrix group " + String.valueOf(resourceCounter);
    }

    static Connection getConnection() throws SQLException {
        String url = getUrl();
        String username = getUsername();
        String password = getPassword();

        return DriverManager.getConnection(url, username, password);
    }

    static String getPassword() {
        return System.getProperty("de.njsm.stocks.internal.db.databasePassword");
    }

    static String getUrl() {
        String address = System.getProperty("de.njsm.stocks.internal.db.databaseAddress");
        String port = System.getProperty("de.njsm.stocks.internal.db.databasePort");
        String name = System.getProperty("de.njsm.stocks.internal.db.databaseName");

        return String.format("jdbc:mariadb://%s:%s/%s?useLegacyDatetimeCode=false&serverTimezone=+00:00",
                address,
                port,
                name);
    }

    static String getUsername() {
        return System.getProperty("de.njsm.stocks.internal.db.databaseUsername");
    }

}
