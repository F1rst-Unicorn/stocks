package de.njsm.stocks.internal.db;

import de.njsm.stocks.data.*;
import de.njsm.stocks.internal.auth.UserContext;

import java.sql.SQLException;
import java.util.Date;

public class SimpleDatabaseHandler implements DatabaseHandler {

    @Override
    public String getNewTicket() throws SQLException {
        return null;
    }

    public void removeFood(UserContext c, int id) throws SQLException {

    }

    public void addFood(UserContext c, Food food) throws SQLException {

    }

    public void addLocation(Location location) throws SQLException {

    }

    public void removeLocation(int id) throws SQLException {

    }

    public void renameLocation(int id, String new_name) throws SQLException {

    }

    public void removeUser(int id) throws SQLException {

    }

    public void removeDevice(int id) throws SQLException {

    }

    public void addFoodItem(UserContext c, FoodItem item) throws SQLException {

    }

    public void removeFoodItem(UserContext c, int id) throws SQLException {

    }

    public void renameFood(UserContext c, int id, String new_name) throws SQLException {

    }

    public Location[] getLocations() throws SQLException {
        return new Location[0];
    }

    public Food[] getFood() throws SQLException {
        return new Food[0];
    }

    public User[] getUsers() throws SQLException {
        return new User[0];
    }

    public UserDevice[] getDevices() throws SQLException {
        return new UserDevice[0];
    }

    public FoodItem[] getFoodItems() throws SQLException {
        return new FoodItem[0];
    }
}
