package de.njsm.stocks.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDeviceFactory extends DataFactory {

    public static final UserDeviceFactory f = new UserDeviceFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM User_device";
    }

    @Override
    public Data createData(ResultSet inputSet) throws SQLException {
        UserDevice result = new UserDevice();
        result.id = inputSet.getInt("ID");
        result.name = inputSet.getString("name");
        result.userId = inputSet.getInt("belongs_to");
        return result;
    }
}
