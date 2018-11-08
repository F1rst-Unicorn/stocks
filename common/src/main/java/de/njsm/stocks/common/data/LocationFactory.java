package de.njsm.stocks.common.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationFactory extends DataFactory<Location> {

    public static final LocationFactory f = new LocationFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Location ORDER BY name, ID";
    }

    @Override
    public Location createData(ResultSet rs) throws SQLException {
        Location l = new Location();
        l.id = rs.getInt("ID");
        l.name = rs.getString("name");
        return l;
    }

}
