package de.njsm.stocks.server.v2.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactoryImpl implements ConnectionFactory {

    private final String url;

    private final String username;

    private final String password;

    public ConnectionFactoryImpl(String url,
                                 String username,
                                 String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public void returnConnection(Connection connection) throws SQLException {
        connection.close();
    }
}
