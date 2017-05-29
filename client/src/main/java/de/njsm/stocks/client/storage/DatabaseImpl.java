package de.njsm.stocks.client.storage;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.visitor.AddStatementVisitor;
import de.njsm.stocks.common.data.visitor.SqlStatementFillerVisitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DatabaseImpl {

    private static final Logger LOG = LogManager.getLogger(DatabaseManager.class);

    void writeData(Connection c, String table, List<? extends Data> data) throws SQLException {
        SqlStatementFillerVisitor stmtFiller = new SqlStatementFillerVisitor();
        AddStatementVisitor sqlCommandGetter = new AddStatementVisitor();

        if (! table.equals("Updates")) {
            clearTable(c, table);
        }
        PreparedStatement insertStmt = null;

        for (Data dataItem : data) {
            if (insertStmt == null) {
                insertStmt = c.prepareStatement(sqlCommandGetter.visit(dataItem));
            }
            stmtFiller.visit(dataItem, insertStmt);
            insertStmt.execute();
        }
    }

    private static void clearTable(Connection c, String name) throws SQLException {
        LOG.info("Clearing table " + name);
        String sqlString = "DELETE FROM " + name;
        PreparedStatement s = c.prepareStatement(sqlString);
        s.execute();
    }
}
