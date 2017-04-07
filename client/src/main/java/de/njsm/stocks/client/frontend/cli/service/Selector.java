package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.view.UserDeviceView;

import java.util.List;

public class Selector {

    protected ScreenWriter writer;

    protected InputReader reader;

    public Selector(ScreenWriter writer, InputReader reader) {
        this.writer = writer;
        this.reader = reader;
    }

    public User selectUser(List<User> users, String name) throws InputException {
        if (users.size() == 1) {
            return users.get(0);
        } else if (users.size() == 0) {
            throw new InputException("No such user found: " + name);
        } else {
            writer.printUsers("Several users found", users);
            int resultId = reader.nextInt("Choose one ", users.get(0).id);
            User result = users.stream().filter(i -> i.id == resultId).findFirst().orElse(null);
            if (result == null) {
                throw new InputException("You did an invalid selection");
            } else {
                return result;
            }
        }
    }

    public Food selectFood(List<Food> f, String name) throws InputException {
        if (f.size() == 1) {
            return f.get(0);
        } else if (f.size() == 0) {
            throw new InputException("No such food found: " + name);
        } else {
            writer.printFood("Several food types found", f);
            int resultId = reader.nextInt("Choose one ", f.get(0).id);
            Food result = f.stream().filter(food -> food.id == resultId).findFirst().orElse(null);
            if (result == null) {
                throw new InputException("You did an invalid selection");
            } else {
                return result;
            }
        }
    }

    public FoodItem selectItem(List<FoodItem> items) throws InputException {
        if (items.isEmpty()) {
            throw new InputException("No items found");
        } else if (items.size() == 1) {
            return items.get(0);
        } else {
            writer.printItems("Several items found: ", items);
            int resultId = reader.nextInt("Choose one ", items.get(0).id);
            FoodItem result = items.stream().filter(i -> i.id == resultId).findFirst().orElse(null);
            if (result == null) {
                throw new InputException("You did an invalid selection");
            } else {
                return result;
            }
        }
    }

    public Location selectLocation(List<Location> l, String name) throws InputException {
        if (l.size() == 1) {
            return l.get(0);
        } else if (l.size() == 0) {
            throw new InputException("No such location found: " + name);
        } else {
            writer.printLocations("Several locations found", l);
            int resultId = reader.nextInt("Choose one ", l.get(0).id);
            Location result = l.stream().filter(i -> i.id == resultId).findFirst().orElse(null);
            if (result == null) {
                throw new InputException("You did an invalid selection");
            } else {
                return result;
            }
        }
    }


    public UserDeviceView selectDevice(List<UserDeviceView> d, String name) throws InputException {
        if (d.size() == 1) {
            return d.get(0);
        } else if (d.size() == 0) {
            throw new InputException("No such device found: " + name);
        } else {
            writer.printUserDeviceViews("Several devices found", d);
            int resultId = reader.nextInt("Choose one ", d.get(0).id);
            UserDeviceView result = d.stream().filter(i -> i.id == resultId).findFirst().orElse(null);
            if (result == null) {
                throw new InputException("You did an invalid selection");
            } else {
                return result;
            }
        }
    }
}
