package de.njsm.stocks.common.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDeviceFactory extends DataFactory<UserDevice> {

    public static final UserDeviceFactory f = new UserDeviceFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM User_device";
    }

    @Override
    public UserDevice createData(ResultSet rs) throws SQLException {
        UserDevice d = new UserDevice();
        d.id = rs.getInt("ID");
        d.name = rs.getString("name");
        d.userId = rs.getInt("belongs_to");
        return d;
    }
}
