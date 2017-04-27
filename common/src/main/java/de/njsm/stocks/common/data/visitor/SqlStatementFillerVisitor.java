package de.njsm.stocks.common.data.visitor;

import de.njsm.stocks.common.data.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SqlStatementFillerVisitor extends StocksDataVisitorImpl<PreparedStatement, Void> {

    @Override
    public Void food(Food food, PreparedStatement input) {
        try {
            input.setInt(1, food.id);
            input.setString(2, food.name);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void foodItem(FoodItem item, PreparedStatement input) {
        try {
            input.setInt(1, item.id);
            input.setDate(2, new java.sql.Date(item.eatByDate.getTime()));
            input.setInt(3, item.ofType);
            input.setInt(4, item.storedIn);
            input.setInt(5, item.registers);
            input.setInt(6, item.buys);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void user(User u, PreparedStatement input) {
        try {
            input.setInt(1, u.id);
            input.setString(2, u.name);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void userDevice(UserDevice device, PreparedStatement input) {
        try {
            input.setInt(1, device.id);
            input.setString(2, device.name);
            input.setInt(3, device.userId);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void location(Location location, PreparedStatement input) {
        try {
            input.setInt(1, location.id);
            input.setString(2, location.name);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void update(Update update, PreparedStatement input) {
        try {
            Timestamp t = new Timestamp(update.lastUpdate.getTime());
            input.setTimestamp(1, t);
            input.setString(2, update.table);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }
}
