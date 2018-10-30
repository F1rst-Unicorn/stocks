package de.njsm.stocks.server.v2.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactoryImpl implements ConnectionFactory {

    private final String url;

    private final Properties config;

    public ConnectionFactoryImpl(String url,
                                 Properties config) {
        this.url = url;
        this.config = config;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, config);
    }

    @Override
    public void returnConnection(Connection connection) throws SQLException {
        connection.close();
    }
}
