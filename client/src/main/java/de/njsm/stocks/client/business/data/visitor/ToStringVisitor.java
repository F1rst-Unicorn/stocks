package de.njsm.stocks.client.business.data.visitor;

import de.njsm.stocks.client.business.data.*;
import de.njsm.stocks.client.business.data.view.UserDeviceView;

import java.time.format.DateTimeFormatter;

public class ToStringVisitor extends BaseVisitor<Void, String> {

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
    public String userDeviceView(UserDeviceView device, Void input) {
        return "\t" + device.id + ": " + device.user + "'s " + device.name;
    }
}
