package de.njsm.stocks.client.business.data.visitor;

import de.njsm.stocks.client.business.data.*;
import de.njsm.stocks.client.business.data.view.UserDeviceView;

public abstract class AbstractVisitor<I, O> {

    public O visit(Data d, I arg) {
        return d.accept(this, arg);
    }

    public abstract O food(Food f, I arg);

    public abstract O location(Location l, I arg);

    public abstract O foodItem(FoodItem foodItem, I input);

    public abstract O userDevice(UserDevice userDevice, I arg);

    public abstract O user(User u, I arg);

    public abstract O update(Update update, I arg);


    public abstract O userDeviceView(UserDeviceView u, I arg);
}
