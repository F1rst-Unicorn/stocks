package de.njsm.stocks.internal.db;

import de.njsm.stocks.data.*;
import de.njsm.stocks.internal.auth.UserContext;

import java.util.Date;

public interface DatabaseHandler {

    void addLocation(String name);
    void removeLocation(int id);
    void renameLocation(int id, String new_name);

    void addUser(String name);
    void removeUser(int id);
    void renameUser(int id, String new_name);

    void addDevice(String name, int uid);
    void removeDevice(int id);
    void renameDevice(int id, String new_name);

    void addFoodItem(UserContext c, String name, Date eat_by);
    void removeFoodItem(UserContext c, int id);
    void renameFood(UserContext c, int id, String new_name);


    Location[] getLocations();
    Food[] getFood();
    User[] getUsers();
    UserDevice[] getDevices();
    FoodItem[] getFoodItems();

}
