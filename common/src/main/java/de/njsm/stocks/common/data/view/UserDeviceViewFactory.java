package de.njsm.stocks.common.data.view;

import de.njsm.stocks.common.data.DataFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDeviceViewFactory extends DataFactory<UserDeviceView> {

    public static final UserDeviceViewFactory f = new UserDeviceViewFactory();

    @Override
    public String getQuery() {
        return "SELECT d.id, d.name, u.name as belongs_to, u.ID as belongs_id " +
                "FROM User_device d, User u " +
                "WHERE d.belongs_to=u.ID";
    }

    @Override
    protected UserDeviceView createData(ResultSet rs) throws SQLException {
        UserDeviceView result = new UserDeviceView();
        result.name = rs.getString("name");
        result.id = rs.getInt("ID");
        result.user = rs.getString("belongs_to");
        result.userId = rs.getInt("belongs_id");
        return result;
    }

}
