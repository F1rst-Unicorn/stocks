package de.njsm.stocks.server.internal.db;

import java.io.IOException;
import java.sql.*;

public class DatabaseHelper {

    public static String[] resetProcedure = {
            "SET FOREIGN_KEY_CHECKS = 0",
            "DELETE FROM Food",
            "DELETE FROM Food_item",
            "DELETE FROM User",
            "DELETE FROM User_device",
            "DELETE FROM Ticket",
            "DELETE FROM Location",
            "INSERT INTO Food (ID, name) VALUES (1, 'Carrot'), (2, 'Beer'), (3, 'Cheese')",
            "INSERT INTO Location (ID, name) VALUES (1, 'Fridge') , (2, 'Cupboard')",
            "INSERT INTO User (ID, name) VALUES (1, 'Bob'), (2, 'Alice')",
            "INSERT INTO User_device (ID, name, belongs_to) VALUES (1, 'mobile', 1), (2, 'mobile2', 1), (3, 'laptop', 2), (4, 'pending_device', 2)",
            "INSERT INTO Food_item (ID, eat_by, registers, buys, stored_in, of_type) VALUES" +
                    "(1, '2017-09-24 00:00:00', 3, 2, 1, 2)," +
                    "(2, '2017-09-24 00:00:00', 3, 2, 1, 2)," +
                    "(3, '2017-09-24 00:00:00', 3, 2, 1, 2)",
            "INSERT INTO Ticket (ticket, belongs_device) VALUES ('AAAA', 3)",
            "SET FOREIGN_KEY_CHECKS = 1",
    };

    public static void resetSampleData() throws SQLException {
        Connection c = getConnection();
        Statement stmt = c.createStatement();

        for (String cmd : resetProcedure) {
            stmt.execute(cmd);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url;
        String address = System.getProperty("de.njsm.stocks.internal.db.databaseAddress");
        String port = System.getProperty("de.njsm.stocks.internal.db.databasePort");
        String name = System.getProperty("de.njsm.stocks.internal.db.databaseName");
        String username = System.getProperty("de.njsm.stocks.internal.db.databaseUsername");
        String password = System.getProperty("de.njsm.stocks.internal.db.databasePassword");

        url = String.format("jdbc:mariadb://%s:%s/%s",
                address,
                port,
                name);

        return DriverManager.getConnection(url, username, password);
    }
}
