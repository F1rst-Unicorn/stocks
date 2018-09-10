package de.njsm.stocks.server.v2.db;

import java.sql.Connection;
import java.sql.SQLException;

public class MockConnectionFactory implements ConnectionFactory {

    private Connection connection;

    MockConnectionFactory(Connection connection) {
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
