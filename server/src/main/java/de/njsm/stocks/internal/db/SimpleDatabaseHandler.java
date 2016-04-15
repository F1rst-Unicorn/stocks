package de.njsm.stocks.internal.db;

import de.njsm.stocks.data.*;
import de.njsm.stocks.internal.auth.UserContext;

import java.util.Date;

public class SimpleDatabaseHandler implements DatabaseHandler {

    public void addLocation(String name) {

    }

    public void removeLocation(int id) {

    }

    public void renameLocation(int id, String new_name) {

    }

    public void addUser(String name) {

    }

    public void removeUser(int id) {

    }

    public void renameUser(int id, String new_name) {

    }

    public void addDevice(String name, int uid) {

    }

    public void removeDevice(int id) {

    }

    public void renameDevice(int id, String new_name) {

    }

    public void addFoodItem(UserContext c, String name, Date eat_by) {

    }

    public void removeFoodItem(UserContext c, int id) {

    }

    public void renameFood(UserContext c, int id, String new_name) {

    }

    public Location[] getLocations() {
        return new Location[0];
    }

    public Food[] getFood() {
        return new Food[0];
    }

    public User[] getUsers() {
        return new User[0];
    }

    public UserDevice[] getDevices() {
        return new UserDevice[0];
    }

    public FoodItem[] getFoodItems() {
        return new FoodItem[0];
    }
}
