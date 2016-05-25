package de.njsm.stocks.linux.client.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseOperator {

    protected Connection c;

    public DatabaseOperator(Connection c) {
        this.c = c;
    }

    public void clearTable(String name) throws SQLException {
        String sqlString = "DELETE FROM " + name;
        PreparedStatement s = c.prepareStatement(sqlString);
        s.execute();
    }
}
