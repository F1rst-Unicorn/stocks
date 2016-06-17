package de.njsm.stocks.client.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SqlRenamable {

    void fillRenameStmt(PreparedStatement stmt, String newName) throws SQLException;

    String getRenameStmt();
}
