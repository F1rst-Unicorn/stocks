package de.njsm.stocks.common.data.visitor;

import de.njsm.stocks.common.data.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlAddVisitor extends StocksDataVisitorImpl<PreparedStatement, Void> {

    @Override
    public Void food(Food food, PreparedStatement input) throws VisitorException {
        try {
            input.setString(1, food.name);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void foodItem(FoodItem item, PreparedStatement input) throws VisitorException {
        try {
            input.setDate(1, new java.sql.Date(item.eatByDate.getTime()));
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
    public Void user(User u, PreparedStatement input) throws VisitorException {
        try {
            input.setString(1, u.name);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void userDevice(UserDevice device, PreparedStatement input) throws VisitorException {
        try {
            input.setString(1, device.name);
            input.setInt(2, device.userId);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }

    @Override
    public Void location(Location location, PreparedStatement input) throws VisitorException {
        try {
            input.setString(1, location.name);
        } catch (SQLException e) {
            throw new VisitorException(e);
        }
        return null;
    }
}
