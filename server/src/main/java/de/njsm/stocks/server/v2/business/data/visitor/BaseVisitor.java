package de.njsm.stocks.server.v2.business.data.visitor;

import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.business.data.ServerTicket;

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
    public O eanNumber(EanNumber n, I arg) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public O serverTicket(ServerTicket t, I arg) {
        throw new RuntimeException("Not implemented");
    }
}
