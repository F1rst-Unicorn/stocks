package de.njsm.stocks.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationFactory extends DataFactory {

    public static final LocationFactory f = new LocationFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Location";
    }

    @Override
    public Data createData(ResultSet inputSet) throws SQLException {
        Location result = new Location();
        result.id = inputSet.getInt("ID");
        result.name = inputSet.getString("name");
        return result;
    }
}
