package de.njsm.stocks.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FoodFactory extends DataFactory {

    public static final FoodFactory f = new FoodFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Food";
    }

    @Override
    public Data createData(ResultSet rs) throws SQLException {
        Food f = new Food();
        f.id = rs.getInt("ID");
        f.name = rs.getString("name");
        return f;
    }
}
