package de.njsm.stocks.server.v1.internal.data.visitor;

import de.njsm.stocks.server.v1.internal.data.*;

public class StocksDataVisitorImpl<I, O> implements StocksDataVisitor<I, O> {

    @Override
    public O visit(Data data, I input) {
        return data.accept(this, input);
    }

    @Override
    public O food(Food food, I input) {
        return null;
    }

    @Override
    public O foodItem(FoodItem item, I input) {
        return null;
    }

    @Override
    public O user(User u, I input) {
        return null;
    }

    @Override
    public O userDevice(UserDevice device, I input) {
        return null;
    }

    @Override
    public O location(Location location, I input) {
        return null;
    }

    @Override
    public O update(Update update, I input) {
        return null;
    }

    @Override
    public O ticket(Ticket t, I input) {
        return null;
    }

    @Override
    public O eanNumber(EanNumber number, I input) {
        return null;
    }

    @Override
    public O serverTicket(ServerTicket serverTicket, I input) {
        return null;
    }
}
