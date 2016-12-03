package de.njsm.stocks.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FoodItemFactory extends DataFactory {

    public static final FoodItemFactory f = new FoodItemFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Food_item";
    }

    @Override
    public Data createData(ResultSet inputSet) throws SQLException {
        FoodItem result = new FoodItem();
        result.id = inputSet.getInt("ID");
        result.eatByDate = inputSet.getDate("eat_by");
        result.ofType = inputSet.getInt("of_type");
        result.storedIn = inputSet.getInt("stored_in");
        result.registers = inputSet.getInt("registers");
        result.buys = inputSet.getInt("buys");
        return result;
    }
}
