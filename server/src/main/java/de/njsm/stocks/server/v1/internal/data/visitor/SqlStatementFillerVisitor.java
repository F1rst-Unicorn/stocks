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
