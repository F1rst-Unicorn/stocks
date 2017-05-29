package de.njsm.stocks.common.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FoodFactory extends DataFactory<Food> {

    public static final FoodFactory f = new FoodFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Food";
    }

    @Override
    public Food createData(ResultSet rs) throws SQLException {
        Food f = new Food();
        f.id = rs.getInt("ID");
        f.name = rs.getString("name");
        return f;
    }
}
