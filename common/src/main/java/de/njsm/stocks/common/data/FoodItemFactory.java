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

package de.njsm.stocks.common.data;

import org.threeten.bp.Instant;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FoodItemFactory extends DataFactory<FoodItem> {

    public static final FoodItemFactory f = new FoodItemFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Food_item ORDER BY eat_by, ID";
    }

    @Override
    public FoodItem createData(ResultSet rs) throws SQLException {
        FoodItem i = new FoodItem();
        i.id = rs.getInt("ID");
        i.eatByDate = Instant.ofEpochMilli(rs.getTimestamp("eat_by").getTime());
        i.ofType = rs.getInt("of_type");
        i.storedIn = rs.getInt("stored_in");
        i.registers = rs.getInt("registers");
        i.buys = rs.getInt("buys");
        return i;
    }
}
