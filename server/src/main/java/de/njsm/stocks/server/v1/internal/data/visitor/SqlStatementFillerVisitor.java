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

package de.njsm.stocks.server.v1.internal.data.visitor;

import de.njsm.stocks.server.v1.internal.data.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

public class SqlStatementFillerVisitor extends StocksDataVisitorImpl<PreparedStatement, Void> {

    @Override
    public Void food(Food food, PreparedStatement input) {
        try {
            input.setString(1, food.name);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void foodItem(FoodItem item, PreparedStatement input) {
        try {
            input.setDate(1, new java.sql.Date(item.eatByDate.toEpochMilli()), Calendar.getInstance(TimeZone.getTimeZone("UTC")));
            input.setInt(2, item.ofType);
            input.setInt(3, item.storedIn);
            input.setInt(4, item.registers);
            input.setInt(5, item.buys);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void user(User u, PreparedStatement input) {
        try {
            input.setString(1, u.name);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void userDevice(UserDevice device, PreparedStatement input) {
        try {
            input.setString(1, device.name);
            input.setInt(2, device.userId);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void location(Location location, PreparedStatement input) {
        try {
            input.setString(1, location.name);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void update(Update update, PreparedStatement input) {
        return null;
    }

    @Override
    public Void eanNumber(EanNumber number, PreparedStatement input) {
        try {
            input.setString(1, number.eanCode);
            input.setInt(2, number.identifiesFood);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void ticket(Ticket t, PreparedStatement input) {
        try {
            input.setString(1, t.ticket);
            input.setInt(2, t.deviceId);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }
}
