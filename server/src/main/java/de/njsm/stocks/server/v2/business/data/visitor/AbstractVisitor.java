package de.njsm.stocks.server.v2.business.data.visitor;

import de.njsm.stocks.server.v2.business.data.*;

public abstract class AbstractVisitor<I, O> {

    public O visit(Data d, I arg) {
        return d.accept(this, arg);
    }

    public abstract O food(Food f, I arg);

    public abstract O location(Location l, I arg);

    public abstract O eanNumber(EanNumber n, I arg);

    public abstract O serverTicket(ServerTicket t, I arg);

    public abstract O foodItem(FoodItem foodItem, I input);

    public abstract O userDevice(UserDevice userDevice, I arg);
}
