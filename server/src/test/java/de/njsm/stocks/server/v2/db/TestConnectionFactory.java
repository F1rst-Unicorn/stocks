package de.njsm.stocks.server.v2.db;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnectionFactory implements ConnectionFactory {

    private Connection connection;

    public TestConnectionFactory(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public void returnConnection(Connection connection) throws SQLException {
    }
}
