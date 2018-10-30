package de.njsm.stocks.server.v1.internal.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EanNumberFactory extends DataFactory {

    public static final EanNumberFactory f = new EanNumberFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM \"EAN_number\"";
    }

    @Override
    protected Data createData(ResultSet inputSet) throws SQLException {
        EanNumber result = new EanNumber();
        result.id = inputSet.getInt("ID");
        result.eanCode = inputSet.getString("number");
        result.identifiesFood = inputSet.getInt("identifies");
        return result;
    }
}
