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

package de.njsm.stocks.server.v1.internal.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FoodFactory extends DataFactory<Food> {

    public static final FoodFactory f = new FoodFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM \"Food\"";
    }

    @Override
    public Food createData(ResultSet rs) throws SQLException {
        Food f = new Food();
        f.id = rs.getInt("ID");
        f.name = rs.getString("name");
        return f;
    }
}
