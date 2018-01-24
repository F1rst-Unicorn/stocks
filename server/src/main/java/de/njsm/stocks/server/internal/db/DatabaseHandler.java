package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.common.data.*;

import java.util.List;

public interface DatabaseHandler {

    int add(Data d);

    void rename(SqlRenamable d, String newName);

    void remove(SqlRemovable d);

    Data[] get(DataFactory f);

    void moveItem(FoodItem item, int loc);

    List<Integer> getDeviceIdsOfUser(User u);

    ServerTicket getTicket(String ticket);

    Principals getPrincipalsForTicket(String ticket);
}
