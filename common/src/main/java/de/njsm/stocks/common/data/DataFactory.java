package de.njsm.stocks.common.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class DataFactory<T extends Data> {

    public abstract String getQuery();

    protected abstract T createData(ResultSet rs) throws SQLException;

    public List<T> createDataList(ResultSet rs) throws SQLException {
        ArrayList<T> result = new ArrayList<>();
        while (rs.next()) {
            result.add(createData(rs));
        }
        return result;
    }

}
