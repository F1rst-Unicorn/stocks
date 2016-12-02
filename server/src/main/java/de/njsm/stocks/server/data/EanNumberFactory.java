package de.njsm.stocks.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EanNumberFactory extends DataFactory {

    public static final EanNumberFactory f = new EanNumberFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM EAN_Number";
    }

    @Override
    protected Data createData(ResultSet rs) throws SQLException {
        EanNumber n = new EanNumber();
        n.id = rs.getInt("ID");
        n.eanCode = rs.getString("number");
        n.identifiesFood = rs.getInt("identifies");
        return n;
    }
}
