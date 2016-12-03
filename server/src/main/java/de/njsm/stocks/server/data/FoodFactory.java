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
    public Data createData(ResultSet inputSet) throws SQLException {
        Food result = new Food();
        result.id = inputSet.getInt("ID");
        result.name = inputSet.getString("name");
        return result;
    }
}
