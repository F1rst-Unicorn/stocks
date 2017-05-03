package de.njsm.stocks.common.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdateFactory extends DataFactory {

    public static final UpdateFactory f = new UpdateFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM Updates";
    }

    @Override
    public Data createData(ResultSet rs) throws SQLException {
        return createUpdateTyped(rs);
    }

    public List<Update> createUpdateList(ResultSet rs) throws SQLException {
        ArrayList<Update> result = new ArrayList<>();
        while (rs.next()) {
            result.add(createUpdateTyped(rs));
        }
        return result;
    }

    private Update createUpdateTyped(ResultSet rs) throws SQLException {
        Update u = new Update();
        u.table = rs.getString("table_name");
        u.lastUpdate = new Date(rs.getTimestamp("last_update").getTime());
        return u;
    }
}
