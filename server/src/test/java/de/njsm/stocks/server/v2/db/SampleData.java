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
            "delete from food_item",
            "delete from ticket",
            "delete from user_device where id != 1",
            "delete from ean_number",
            "delete from food",
            "delete from \"user\" where id != 1",
            "delete from location",
            "delete from unit where id != 1",
            "delete from scaled_unit where id != 1",
            "delete from recipe",
            "delete from recipe_ingredient",
            "delete from recipe_product",

            "alter sequence \"Food_item_ID_seq\" restart",
            "alter sequence \"Food_ID_seq\" restart",
            "alter sequence \"User_device_ID_seq\" restart with 2",
            "alter sequence \"User_ID_seq\" restart with 2",
            "alter sequence \"Location_ID_seq\" restart",
            "alter sequence \"Ticket_ID_seq\" restart",
            "alter sequence \"EAN_number_ID_seq\" restart",
            "alter sequence unit_id_seq restart with 2",
            "alter sequence scaled_unit_id_seq restart with 2",
            "alter sequence recipe_id_seq restart",
            "alter sequence recipe_ingredient_id_seq restart",
            "alter sequence recipe_product_id_seq restart",

            "insert into location (name, description, initiates) values " +
                    "('Fridge', 'fridge description', 1), " +
                    "('Cupboard', 'cupboard description', 1)",
            "insert into food (name, to_buy, location, description, initiates, expiration_offset, store_unit) values " +
                    "('Carrot', false, null, 'carrot description',  1, '2 days', 1), " +
                    "('Beer', true, null, 'beer description', 1, '0 days', 1), " +
                    "('Cheese', false, 1, '', 1, '3 days', 1)",
            "insert into \"user\" (name, initiates) values " +
                    "('Bob', 1), " +
                    "('Alice', 1), " +
                    "('Jack', 1)",
            "insert into user_device (name, belongs_to, initiates) values " +
                    "('mobile', 2, 1), " +
                    "('mobile2', 2, 1), " +
                    "('laptop', 3, 1), " +
                    "('pending_device', 3, 1)",
            "insert into food_item (eat_by, registers, buys, stored_in, of_type, initiates, unit) values" +
                    "('1970-01-01 00:00:00+00', 3, 2, 1, 2, 1, 1)," +
                    "('1970-01-01 00:00:00+00', 3, 2, 1, 2, 1, 1)," +
                    "('1970-01-01 00:00:00+00', 3, 2, 1, 2, 1, 1)",
            "insert into ticket (ticket, belongs_device) values " +
                    "('AAAA', 5)",
            "insert into ean_number (number, identifies, initiates) values " +
                    "('EAN BEER', 2, 1)",
            "insert into unit (name, abbreviation, initiates) values" +
                    "('Liter', 'l', 1)," +
                    "('Unit', 'u', 1)",
            "insert into scaled_unit (scale, unit, initiates) values" +
                    "(3, 2, 1)," +
                    "(3, 1, 1)",
            "insert into recipe (name, instructions, duration, initiates) values " +
                    "('Cake', 'Mix flour and sugar. Bake directly', interval '1 hour', 1)," +
                    "('Bread', 'Mix flour and water. Bake with love', interval '2 hour', 1)",
            "insert into recipe_ingredient (amount, ingredient, recipe, unit, initiates) values " +
                    "(2, 3, 1, 2, 1)",
            "insert into recipe_product (amount, product, recipe, unit, initiates) values " +
                    "(2, 3, 1, 2, 1)"

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
