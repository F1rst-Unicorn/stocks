package de.njsm.stocks.client.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SqlRemovable {

    void fillRemoveStmt(PreparedStatement stmt) throws SQLException;

    String getRemoveStmt();
}
