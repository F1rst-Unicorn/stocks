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

import java.sql.ResultSet;
import java.sql.SQLException;

public class EanNumberFactory extends DataFactory {

    public static final EanNumberFactory f = new EanNumberFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM EAN_number ORDER BY number, ID";
    }

    @Override
    protected Data createData(ResultSet inputSet) throws SQLException {
        EanNumber result = new EanNumber();
        result.id = inputSet.getInt("ID");
        result.eanCode = inputSet.getString("number");
        result.identifiesFood = inputSet.getInt("identifies");
        return result;
    }
}
