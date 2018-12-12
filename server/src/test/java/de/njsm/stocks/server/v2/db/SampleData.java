package de.njsm.stocks.server.v2.db;

import java.sql.*;

class SampleData {

    private static final String[] sampleDbData = {
            "DELETE FROM \"Food_item\"",
            "DELETE FROM \"Ticket\"",
            "DELETE FROM \"User_device\"",
            "DELETE FROM \"EAN_number\"",
            "DELETE FROM \"Food\"",
            "DELETE FROM \"User\"",
            "DELETE FROM \"Location\"",

            "ALTER SEQUENCE \"Food_item_ID_seq\" RESTART",
            "ALTER SEQUENCE \"Food_ID_seq\" RESTART",
            "ALTER SEQUENCE \"User_device_ID_seq\" RESTART",
            "ALTER SEQUENCE \"User_ID_seq\" RESTART",
            "ALTER SEQUENCE \"Location_ID_seq\" RESTART",
            "ALTER SEQUENCE \"Ticket_ID_seq\" RESTART",
            "ALTER SEQUENCE \"EAN_number_ID_seq\" RESTART",

            "INSERT INTO \"Food\" (\"name\") VALUES " +
                    "('Carrot'), " +
                    "('Beer'), " +
                    "('Cheese')",
            "INSERT INTO \"Location\" (\"name\") VALUES " +
                    "('Fridge'), " +
                    "('Cupboard')",
            "INSERT INTO \"User\" (\"name\") VALUES " +
                    "('Bob'), " +
                    "('Alice'), " +
                    "('Jack')",
            "INSERT INTO \"User_device\" (\"name\", \"belongs_to\") VALUES " +
                    "('mobile', 1), " +
                    "('mobile2', 1), " +
                    "('laptop', 2), " +
                    "('pending_device', 2)",
            "INSERT INTO \"Food_item\" (\"eat_by\", \"registers\", \"buys\", \"stored_in\", \"of_type\") VALUES" +
                    "('1970-01-01 00:00:00+00', 3, 2, 1, 2)," +
                    "('1970-01-01 00:00:00+00', 3, 2, 1, 2)," +
                    "('1970-01-01 00:00:00+00', 3, 2, 1, 2)",
            "INSERT INTO \"Ticket\" (\"ticket\", \"belongs_device\") VALUES " +
                    "('AAAA', 3)",
            "INSERT INTO \"EAN_number\" (\"number\", \"identifies\") VALUES " +
                    "('EAN BEER', 2)",
    };

    static void insertSampleData(Connection c) throws SQLException {
        Statement stmt = c.createStatement();

        for (String cmd : sampleDbData) {
            stmt.execute(cmd);
        }
    }

}