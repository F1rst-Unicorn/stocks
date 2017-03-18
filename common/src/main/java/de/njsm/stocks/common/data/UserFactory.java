package de.njsm.stocks.common.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserFactory extends DataFactory {

    public static final UserFactory f = new UserFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM User";
    }

    @Override
    public Data createData(ResultSet rs) throws SQLException {
        return createUserTyped(rs);
    }

    public List<User> createUserList(ResultSet rs) throws SQLException {
        ArrayList<User> result = new ArrayList<>();
        while (rs.next()) {
            result.add(createUserTyped(rs));
        }
        return result;
    }

    private User createUserTyped(ResultSet rs) throws SQLException {
        User u = new User();
        u.id = rs.getInt("ID");
        u.name = rs.getString("name");
        return u;
    }
}
