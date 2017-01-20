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
    public Data createData(ResultSet inputSet) throws SQLException {
        Update result = new Update();
        result.table = inputSet.getString("table_name");
        result.lastUpdate = inputSet.getTimestamp("last_update");
        return result;
    }
}
