package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.common.data.*;

public interface DatabaseHandler {

    void add(Data d);
    void rename(SqlRenamable d, String newName);
    void remove(SqlRemovable d);
    Data[] get(DataFactory f);

    void moveItem(FoodItem item, int loc);

    void removeUser(User u);
    Ticket addDevice(UserDevice d);
    void removeDevice(UserDevice d);
}
