package de.njsm.stocks.client.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class DataFactory {

    public abstract String getQuery();

    protected abstract Data createData(ResultSet rs) throws SQLException;

    public List<Data> createDataList(ResultSet rs) throws SQLException {
        ArrayList<Data> result = new ArrayList<>();
        while (rs.next()) {
            result.add(createData(rs));
        }
        return result;
    }

}
