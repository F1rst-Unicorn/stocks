package de.njsm.stocks.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateFactory extends DataFactory {

    public static final UpdateFactory f = new UpdateFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Updates";
    }

    @Override
    public Data createData(ResultSet rs) throws SQLException {
        Update u = new Update();
        u.table = rs.getString("table_name");
        u.lastUpdate = rs.getTimestamp("last_update");
        return u;
    }
}
