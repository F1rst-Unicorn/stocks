package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.server.data.*;
import de.njsm.stocks.server.internal.auth.Principals;

import java.sql.SQLException;

public interface DatabaseHandler {

    void addLocation(Location location) throws SQLException;
    void removeLocation(int id)  throws SQLException;
    void renameLocation(int id, String new_name)  throws SQLException;

    void addUser(User u) throws SQLException;
    void removeUser(int id)  throws SQLException;

    Ticket addDevice(UserDevice d) throws SQLException;
    void removeDevice(int id)  throws SQLException;

    void addFood(Food food) throws SQLException;
    void removeFood(int id) throws SQLException;
    void renameFood(int id, String new_name)  throws SQLException;

    void addFoodItem(Principals c, FoodItem item)  throws SQLException;
    void removeFoodItem(int id)  throws SQLException;

    Location[] getLocations()  throws SQLException;
    Food[] getFood()  throws SQLException;
    User[] getUsers() throws SQLException;
    UserDevice[] getDevices() throws SQLException;
    FoodItem[] getFoodItems() throws SQLException;
    Update[] getUpdates() throws SQLException;

}
