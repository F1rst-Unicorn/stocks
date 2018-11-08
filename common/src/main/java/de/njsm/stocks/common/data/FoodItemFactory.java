package de.njsm.stocks.common.data;

import org.threeten.bp.Instant;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FoodItemFactory extends DataFactory<FoodItem> {

    public static final FoodItemFactory f = new FoodItemFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Food_item ORDER BY eat_by, ID";
    }

    @Override
    public FoodItem createData(ResultSet rs) throws SQLException {
        FoodItem i = new FoodItem();
        i.id = rs.getInt("ID");
        i.eatByDate = Instant.ofEpochMilli(rs.getTimestamp("eat_by").getTime());
        i.ofType = rs.getInt("of_type");
        i.storedIn = rs.getInt("stored_in");
        i.registers = rs.getInt("registers");
        i.buys = rs.getInt("buys");
        return i;
    }
}
