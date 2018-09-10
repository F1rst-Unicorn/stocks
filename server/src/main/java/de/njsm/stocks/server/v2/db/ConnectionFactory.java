package de.njsm.stocks.server.v2.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    Connection getConnection() throws SQLException;

    void returnConnection(Connection connection) throws SQLException;
}
