package de.njsm.stocks.common.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SqlRenamable {

    void fillRenameStmt(PreparedStatement stmt, String newName) throws SQLException;

    String getRenameStmt();
}
