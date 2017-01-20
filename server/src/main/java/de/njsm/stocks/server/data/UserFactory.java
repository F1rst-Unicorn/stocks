package de.njsm.stocks.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFactory extends DataFactory {

    public static final UserFactory f = new UserFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM User";
    }

    @Override
    public Data createData(ResultSet inputSet) throws SQLException {
        User result = new User();
        result.id = inputSet.getInt("ID");
        result.name = inputSet.getString("name");
        return result;
    }
}
