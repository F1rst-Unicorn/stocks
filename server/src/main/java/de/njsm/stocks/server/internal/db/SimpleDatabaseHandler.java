package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.server.internal.auth.Principals;
import de.njsm.stocks.server.data.*;

import java.sql.SQLException;

public class SimpleDatabaseHandler implements DatabaseHandler {

    @Override
    public void addLocation(Location location) throws SQLException {

    }

    @Override
    public void removeLocation(int id) throws SQLException {

    }

    @Override
    public void renameLocation(int id, String new_name) throws SQLException {

    }

    @Override
    public void addUser(User u) throws SQLException {

    }

    @Override
    public void removeUser(int id) throws SQLException {

    }

    @Override
    public Ticket addDevice(UserDevice d) throws SQLException {
        return null;
    }

    @Override
    public void removeDevice(int id) throws SQLException {

    }

    @Override
    public void addFood(Food food) throws SQLException {

    }

    @Override
    public void removeFood(int id) throws SQLException {

    }

    @Override
    public void renameFood(int id, String new_name) throws SQLException {

    }

    @Override
    public void addFoodItem(Principals c, FoodItem item) throws SQLException {

    }

    @Override
    public void removeFoodItem(int id) throws SQLException {

    }

    @Override
    public Location[] getLocations() throws SQLException {
        return new Location[0];
    }

    @Override
    public Food[] getFood() throws SQLException {
        return new Food[0];
    }

    @Override
    public User[] getUsers() throws SQLException {
        return new User[0];
    }

    @Override
    public UserDevice[] getDevices() throws SQLException {
        return new UserDevice[0];
    }

    @Override
    public FoodItem[] getFoodItems() throws SQLException {
        return new FoodItem[0];
    }

    @Override
    public Update[] getUpdates() throws SQLException {
        return new Update[0];
    }
}
