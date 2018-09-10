package de.njsm.stocks.server.v2.db;

import java.sql.*;

class SampleData {

    private static final String[] sampleDbData = {
            "SET FOREIGN_KEY_CHECKS = 0",
            "DELETE FROM Food",
            "DELETE FROM Food_item",
            "DELETE FROM User",
            "DELETE FROM User_device",
            "DELETE FROM Ticket",
            "DELETE FROM Location",
            "DELETE FROM EAN_number",
            "INSERT INTO Food (ID, name) VALUES (1, 'Carrot'), (2, 'Beer'), (3, 'Cheese')",
            "INSERT INTO Location (ID, name) VALUES (1, 'Fridge') , (2, 'Cupboard')",
            "INSERT INTO User (ID, name) VALUES (1, 'Bob'), (2, 'Alice'), (3, 'Jack')",
            "INSERT INTO User_device (ID, name, belongs_to) VALUES (1, 'mobile', 1), (2, 'mobile2', 1), (3, 'laptop', 2), (4, 'pending_device', 2)",
            "INSERT INTO Food_item (ID, eat_by, registers, buys, stored_in, of_type) VALUES" +
                    "(1, '1970-01-01 00:00:00', 3, 2, 1, 2)," +
                    "(2, '1970-01-01 00:00:00', 3, 2, 1, 2)," +
                    "(3, '1970-01-01 00:00:00', 3, 2, 1, 2)",
            "INSERT INTO Ticket (ticket, belongs_device) VALUES ('AAAA', 3)",
            "INSERT INTO EAN_number (ID, number, identifies) VALUES (1, 'EAN BEER', 2)",
            "SET FOREIGN_KEY_CHECKS = 1",
    };

    static void insertSampleData(Connection c) throws SQLException {
        Statement stmt = c.createStatement();

        for (String cmd : sampleDbData) {
            stmt.execute(cmd);
        }
    }

}
