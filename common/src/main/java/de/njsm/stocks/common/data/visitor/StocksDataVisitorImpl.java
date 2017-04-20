package de.njsm.stocks.common.data.visitor;

import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.view.UserDeviceView;

public class StocksDataVisitorImpl<I, O> implements StocksDataVisitor<I, O> {

    @Override
    public O visit(Data data, I input) throws VisitorException {
        return data.accept(this, input);
    }

    @Override
    public O food(Food food, I input) throws VisitorException {
        return null;
    }

    @Override
    public O foodItem(FoodItem item, I input) throws VisitorException {
        return null;
    }

    @Override
    public O user(User u, I input) throws VisitorException {
        return null;
    }

    @Override
    public O userDevice(UserDevice device, I input) throws VisitorException {
        return null;
    }

    @Override
    public O location(Location location, I input) throws VisitorException {
        return null;
    }

    @Override
    public O update(Update update, I input) {
        return null;
    }

    @Override
    public O ticket(Ticket t, I input) throws VisitorException {
        return null;
    }

    @Override
    public O userDeviceView(UserDeviceView userDeviceView, I input) throws VisitorException {
        return null;
    }
}
