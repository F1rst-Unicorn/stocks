/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

            "INSERT INTO \"Location\" (\"name\", initiates) VALUES " +
                    "('Fridge', 1), " +
                    "('Cupboard', 1)",
            "INSERT INTO \"Food\" (\"name\", \"to_buy\", \"location\", initiates) VALUES " +
                    "('Carrot', false, null, 1), " +
                    "('Beer', true, null, 1), " +
                    "('Cheese', false, 1, 1)",
            "INSERT INTO \"User\" (\"name\", initiates) VALUES " +
                    "('Bob', 1), " +
                    "('Alice', 1), " +
                    "('Jack', 1)",
            "INSERT INTO \"User_device\" (\"name\", \"belongs_to\", initiates) VALUES " +
                    "('mobile', 1, 1), " +
                    "('mobile2', 1, 1), " +
                    "('laptop', 2, 1), " +
                    "('pending_device', 2, 1)",
            "INSERT INTO \"Food_item\" (\"eat_by\", \"registers\", \"buys\", \"stored_in\", \"of_type\", initiates) VALUES" +
                    "('1970-01-01 00:00:00+00', 3, 2, 1, 2, 1)," +
                    "('1970-01-01 00:00:00+00', 3, 2, 1, 2, 1)," +
                    "('1970-01-01 00:00:00+00', 3, 2, 1, 2, 1)",
            "INSERT INTO \"Ticket\" (\"ticket\", \"belongs_device\") VALUES " +
                    "('AAAA', 4)",
            "INSERT INTO \"EAN_number\" (\"number\", \"identifies\", initiates) VALUES " +
                    "('EAN BEER', 2, 1)",
    };

    static void insertSampleData(Connection c) throws SQLException {
        c.setAutoCommit(false);
        c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        Statement stmt = c.createStatement();

        for (String cmd : sampleDbData) {
            stmt.execute(cmd);
        }

        c.commit();
    }

}
