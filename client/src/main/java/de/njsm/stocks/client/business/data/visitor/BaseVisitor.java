package de.njsm.stocks.client.business.data.visitor;

import de.njsm.stocks.client.business.data.*;
import de.njsm.stocks.client.business.data.view.UserDeviceView;

public class BaseVisitor<I,O> extends AbstractVisitor<I,O> {

    @Override
    public O food(Food f, I arg) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public O location(Location l, I arg) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public O foodItem(FoodItem foodItem, I input) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public O userDevice(UserDevice userDevice, I arg) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public O user(User u, I arg) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public O update(Update update, I arg) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public O userDeviceView(UserDeviceView u, I arg) {
        throw new RuntimeException("Not implemented");
    }
}
