package de.njsm.stocks.internal.db;

import java.util.Date;

public abstract class AbstractDatabaseHandler {

    public abstract void addLocation(String name);
    public abstract void removeLocation(int id);
    public abstract void renameLocation(int id, String new_name);

    public abstract void addUser(String name);
    public abstract void removeUser(int id);
    public abstract void renameUser(int id, String new_name);

    public abstract void addDevice(String name, int uid);
    public abstract void removeDevice(int id);
    public abstract void renameDevice(int id, String new_name);

    public abstract void addFoodItem(String name, Date eat_by);
    public abstract void removeFoodItem(int id);
    public abstract void renameFood(int id, String new_name);


    public abstract Date getRecestUpdate();



}
