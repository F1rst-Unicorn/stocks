package de.njsm.stocks.internal.db;

import de.njsm.stocks.data.*;
import de.njsm.stocks.internal.auth.UserContext;

import java.sql.SQLException;
import java.util.Date;

public interface DatabaseHandler {

    void addLocation(Location location) throws SQLException;
    void removeLocation(int id)  throws SQLException;
    void renameLocation(int id, String new_name)  throws SQLException;

    void addUser(User u) throws SQLException;
    void removeUser(int id)  throws SQLException;

    String addDevice(UserDevice d) throws SQLException;
    void removeDevice(int id)  throws SQLException;

    void addFood(UserContext c, Food food) throws SQLException;
    void removeFood(UserContext c, int id) throws SQLException;
    void renameFood(UserContext c, int id, String new_name)  throws SQLException;

    void addFoodItem(UserContext c, FoodItem item)  throws SQLException;
    void removeFoodItem(UserContext c, int id)  throws SQLException;

    Location[] getLocations()  throws SQLException;
    Food[] getFood()  throws SQLException;
    User[] getUsers() throws SQLException;
    UserDevice[] getDevices() throws SQLException;
    FoodItem[] getFoodItems() throws SQLException;
    Update[] getUpdates() throws SQLException;

}
