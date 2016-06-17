package de.njsm.stocks.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationFactory extends DataFactory {

    public static LocationFactory f = new LocationFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Location";
    }

    @Override
    public Data createData(ResultSet rs) throws SQLException {
        Location l = new Location();
        l.id = rs.getInt("ID");
        l.name = rs.getString("name");
        return l;
    }
}
