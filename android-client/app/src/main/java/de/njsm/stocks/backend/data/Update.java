package de.njsm.stocks.backend.data;

import java.util.Date;

public class Update {

    public String table;
    public Date lastUpdate;

    public Update(String table, Date lastUpdate) {
        this.table = table;
        this.lastUpdate = lastUpdate;
    }
}
