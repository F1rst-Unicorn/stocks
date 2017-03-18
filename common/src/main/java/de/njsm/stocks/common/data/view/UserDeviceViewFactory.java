package de.njsm.stocks.common.data.view;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.DataFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDeviceViewFactory extends DataFactory {

    public static final UserDeviceViewFactory f = new UserDeviceViewFactory();

    @Override
    public String getQuery() {
        return "SELECT d.id, d.name, u.name as belongs_to " +
                "FROM User_device d, User u " +
                "WHERE d.belongs_to=u.ID";
    }

    @Override
    protected Data createData(ResultSet rs) throws SQLException {
        return createDataTyped(rs);
    }

    public List<UserDeviceView> getViewList(ResultSet rs) throws SQLException {
        ArrayList<UserDeviceView> result = new ArrayList<>();
        while (rs.next()) {
            result.add(createDataTyped(rs));
        }
        return result;
    }

    private UserDeviceView createDataTyped(ResultSet rs) throws SQLException {
        UserDeviceView result = new UserDeviceView();
        result.name = rs.getString("name");
        result.id = rs.getInt("ID");
        result.user = rs.getString("belongs_to");
        return result;
    }
}
