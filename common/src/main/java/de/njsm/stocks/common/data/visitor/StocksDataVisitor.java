package de.njsm.stocks.common.data.visitor;

import de.njsm.stocks.common.data.*;

public interface StocksDataVisitor<I, O> {

    O visit(Data data, I input) throws VisitorException;

    O food(Food food, I input) throws VisitorException;

    O foodItem(FoodItem item, I input) throws VisitorException;

    O user(User u, I input) throws VisitorException;

    O userDevice(UserDevice device, I input) throws VisitorException;

    O location(Location location, I input) throws VisitorException;

    O update(Update update, I input) throws VisitorException;

}
