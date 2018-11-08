package de.njsm.stocks.common.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFactory extends DataFactory<User> {

    public static final UserFactory f = new UserFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM User ORDER BY name, ID";
    }

    @Override
    public User createData(ResultSet rs) throws SQLException {
        User u = new User();
        u.id = rs.getInt("ID");
        u.name = rs.getString("name");
        return u;
    }
}
