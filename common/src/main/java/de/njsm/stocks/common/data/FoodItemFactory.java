package de.njsm.stocks.common.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FoodItemFactory extends DataFactory {

    public static final FoodItemFactory f = new FoodItemFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Food_item";
    }

    @Override
    public Data createData(ResultSet rs) throws SQLException {
        return createDataTyped(rs);
    }

    public List<FoodItem> createFoodItemList(ResultSet rs) throws SQLException {
        List<FoodItem> result = new ArrayList<>();
        while (rs.next()) {
            result.add(createDataTyped(rs));
        }
        return result;
    }

    public FoodItem createDataTyped(ResultSet rs) throws SQLException {
        FoodItem i = new FoodItem();
        i.id = rs.getInt("ID");
        i.eatByDate = rs.getTimestamp("eat_by");
        i.ofType = rs.getInt("of_type");
        i.storedIn = rs.getInt("stored_in");
        i.registers = rs.getInt("registers");
        i.buys = rs.getInt("buys");
        return i;
    }
}
