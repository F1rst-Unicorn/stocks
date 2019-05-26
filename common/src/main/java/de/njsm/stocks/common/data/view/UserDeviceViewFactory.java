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

package de.njsm.stocks.common.data.view;

import de.njsm.stocks.common.data.DataFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDeviceViewFactory extends DataFactory<UserDeviceView> {

    public static final UserDeviceViewFactory f = new UserDeviceViewFactory();

    @Override
    public String getQuery() {
        return "SELECT d.id, d.name, u.name as belongs_to, u.ID as belongs_id " +
                "FROM User_device d, User u " +
                "WHERE d.belongs_to=u.ID " +
                "ORDER BY d.name";
    }

    @Override
    protected UserDeviceView createData(ResultSet rs) throws SQLException {
        UserDeviceView result = new UserDeviceView();
        result.name = rs.getString("name");
        result.id = rs.getInt("ID");
        result.user = rs.getString("belongs_to");
        result.userId = rs.getInt("belongs_id");
        return result;
    }

}
