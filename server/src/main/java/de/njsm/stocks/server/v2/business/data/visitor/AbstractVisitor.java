package de.njsm.stocks.server.v2.business.data.visitor;

import de.njsm.stocks.server.v2.business.data.Data;
import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.business.data.ServerTicket;

public abstract class AbstractVisitor<I, O> {

    public O visit(Data d, I arg) {
        return d.accept(this, arg);
    }

    public abstract O food(Food f, I arg);

    public abstract O location(Location l, I arg);

    public abstract O serverTicket(ServerTicket t, I arg);

}
