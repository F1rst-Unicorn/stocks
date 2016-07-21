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
    public Data createData(ResultSet rs) throws SQLException {
        FoodItem i = new FoodItem();
        i.id = rs.getInt("ID");
        i.eatByDate = rs.getDate("eat_by");
        i.ofType = rs.getInt("of_type");
        i.storedIn = rs.getInt("stored_in");
        i.registers = rs.getInt("registers");
        i.buys = rs.getInt("buys");
        return i;
    }
}
