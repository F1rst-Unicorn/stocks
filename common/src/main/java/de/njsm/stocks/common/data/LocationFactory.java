package de.njsm.stocks.common.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationFactory extends DataFactory {

    public static final LocationFactory f = new LocationFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Location";
    }

    @Override
    public Data createData(ResultSet rs) throws SQLException {
        return createLocationTyped(rs);
    }

    public List<Location> createLocationList(ResultSet rs) throws SQLException {
        ArrayList<Location> result = new ArrayList<>();
        while (rs.next()) {
            result.add(createLocationTyped(rs));
        }
        return result;
    }

    private Location createLocationTyped(ResultSet rs) throws SQLException {
        Location l = new Location();
        l.id = rs.getInt("ID");
        l.name = rs.getString("name");
        return l;
    }
}
