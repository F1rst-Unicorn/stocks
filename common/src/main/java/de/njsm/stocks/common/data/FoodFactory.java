package de.njsm.stocks.common.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FoodFactory extends DataFactory {

    public static final FoodFactory f = new FoodFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Food";
    }

    @Override
    public Data createData(ResultSet rs) throws SQLException {
        return createDataTyped(rs);
    }

    public List<Food> createFoodList(ResultSet rs) throws SQLException {
        List<Food> result = new ArrayList<>();
        while (rs.next()) {
            result.add(createDataTyped(rs));
        }
        return result;
    }

    private Food createDataTyped(ResultSet rs) throws SQLException {
        Food f = new Food();
        f.id = rs.getInt("ID");
        f.name = rs.getString("name");
        return f;
    }

}
