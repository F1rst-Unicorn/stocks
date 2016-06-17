package de.njsm.stocks.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFactory extends DataFactory {

    public static UserFactory f = new UserFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM User";
    }

    @Override
    public Data createData(ResultSet rs) throws SQLException {
        User u = new User();
        u.id = rs.getInt("ID");
        u.name = rs.getString("name");
        return u;
    }
}
