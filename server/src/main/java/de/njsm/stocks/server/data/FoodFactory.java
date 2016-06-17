package de.njsm.stocks.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FoodFactory extends DataFactory {

    public static FoodFactory f = new FoodFactory();

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
