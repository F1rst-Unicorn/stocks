package de.njsm.stocks.server.v1.internal.data.visitor;

import de.njsm.stocks.server.v1.internal.data.*;
import java.time.format.DateTimeFormatter;

public class ToStringVisitor extends StocksDataVisitorImpl<Void, String> {

    private final DateTimeFormatter format;

    public ToStringVisitor(DateTimeFormatter format) {
        this.format = format;
    }

    @Override
    public String food(Food food, Void input) {
        return "\t" + food.id + ": " + food.name;
    }

    @Override
    public String foodItem(FoodItem item, Void input) {
        return "\t\t" + item.id + ": " + format.format(item.eatByDate);
    }

    @Override
    public String user(User u, Void input) {
        return "\t" + u.id + ": " + u.name;
    }

    @Override
    public String userDevice(UserDevice device, Void input) {
        return super.userDevice(device, input);
    }

    @Override
    public String location(Location location, Void input) {
        return "\t" + location.id + ": " + location.name;
    }

    @Override
    public String update(Update update, Void input) {
        return "\t" + update.table + ": " + format.format(update.lastUpdate);
    }

    @Override
    public String ticket(Ticket t, Void input) {
        return super.ticket(t, input);
    }

    @Override
    public String eanNumber(EanNumber number, Void input) {
        return "\t" + number.id + ": " + number.eanCode;
    }
}