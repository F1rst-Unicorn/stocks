package de.njsm.stocks.server.v1.internal.data.visitor;

import de.njsm.stocks.server.v1.internal.data.*;

public interface StocksDataVisitor<I, O> {

    O visit(Data data, I input);

    O food(Food food, I input);

    O foodItem(FoodItem item, I input);

    O user(User u, I input);

    O userDevice(UserDevice device, I input);

    O location(Location location, I input);

    O update(Update update, I input);

    O ticket(Ticket t, I input);

    O eanNumber(EanNumber number, I input);

    O serverTicket(ServerTicket serverTicket, I input);
}
