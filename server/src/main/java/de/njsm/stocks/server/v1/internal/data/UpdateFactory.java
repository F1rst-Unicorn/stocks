package de.njsm.stocks.server.v1.internal.data;

import java.time.Instant;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateFactory extends DataFactory<Update> {

    public static final UpdateFactory f = new UpdateFactory();

    @Override
    public String getQuery() {
        return "SELECT * FROM \"Updates\"";
    }

    @Override
    public Update createData(ResultSet rs) throws SQLException {
        Update u = new Update();
        u.table = rs.getString("table_name");
        u.lastUpdate = Instant.ofEpochMilli(rs.getTimestamp("last_update").getTime());
        return u;
    }
}
