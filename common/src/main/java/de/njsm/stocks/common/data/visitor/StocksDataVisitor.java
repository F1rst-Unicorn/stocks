package de.njsm.stocks.common.data.visitor;

import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.view.UserDeviceView;

public interface StocksDataVisitor<I, O> {

    O visit(Data data, I input);

    O food(Food food, I input);

    O foodItem(FoodItem item, I input);

    O user(User u, I input);

    O userDevice(UserDevice device, I input);

    O location(Location location, I input);

    O update(Update update, I input);

    O ticket(Ticket t, I input);

    O userDeviceView(UserDeviceView userDeviceView, I input);

    O eanNumber(EanNumber number, I input);


}
