package de.njsm.stocks.server.v1.internal.db;

import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.visitor.AddStatementVisitor;
import de.njsm.stocks.common.data.visitor.SqlStatementFillerVisitor;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.FailSafeDatabaseHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SqlDatabaseHandler extends FailSafeDatabaseHandler implements de.njsm.stocks.server.v1.internal.db.DatabaseHandler {

    private AddStatementVisitor addStatementVisitor;

    private SqlStatementFillerVisitor fillerVisitor;

    public SqlDatabaseHandler(String url, String username, String password, String resourceIdentifier) {
        super(url, username, password, resourceIdentifier);
        this.addStatementVisitor = new AddStatementVisitor();
        this.fillerVisitor = new SqlStatementFillerVisitor();
    }

    @Override
    public int add(Data d) {
        return runSqlOperation(con -> {
            PreparedStatement stmt = con.prepareStatement(addStatementVisitor.visit(d, null));
            fillerVisitor.visit(d, stmt);
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        });
    }

    @Override
    public void rename(SqlRenamable d, String newName) {
        runSqlOperation(con -> {
            PreparedStatement stmt = con.prepareStatement(d.getRenameStmt());
            d.fillRenameStmt(stmt, newName);
            stmt.execute();
        });
    }

    @Override
    public void remove(SqlRemovable d) {
        runSqlOperation(con -> {
            PreparedStatement stmt = con.prepareStatement(d.getRemoveStmt());
            d.fillRemoveStmt(stmt);
            stmt.execute();
        });
    }

    @Override
    public Data[] get(DataFactory df) {
        return runSqlOperation(con -> {
            PreparedStatement stmt = con.prepareStatement(df.getQuery());
            ResultSet rs = stmt.executeQuery();
            List<Data> result = df.createDataList(rs);
            return result.toArray(new Data[result.size()]);
        });
    }

    @Override
    public List<Integer> getDeviceIdsOfUser(User u) {
        return runSqlOperation(con -> {
            String getDevicesQuery = "SELECT * FROM User_device WHERE belongs_to=?";
            PreparedStatement sqlQuery = con.prepareStatement(getDevicesQuery);
            List<Integer> certificateList = new ArrayList<>();
            sqlQuery.setInt(1, u.id);
            ResultSet res = sqlQuery.executeQuery();
            while (res.next()) {
                certificateList.add(res.getInt("ID"));
            }
            return certificateList;
        });
    }

    @Override
    public void moveItem(FoodItem item, int loc) {
        runSqlOperation(con -> {
                String sqlString = "UPDATE Food_item SET stored_in=? WHERE ID=?";
                PreparedStatement stmt = con.prepareStatement(sqlString);
                stmt.setInt(1, loc);
                stmt.setInt(2, item.id);
                stmt.executeQuery();
        });
    }

    @Override
    public ServerTicket getTicket(String ticket) {
        return runSqlOperation(con -> {
            String query = "SELECT * FROM Ticket WHERE ticket=?";
            PreparedStatement sqlQuery = con.prepareStatement(query);
            sqlQuery.setString(1, ticket);

            ServerTicket result = null;
            ResultSet rs = sqlQuery.executeQuery();
            while (rs.next()) {
                result = new ServerTicket(0,
                        rs.getTimestamp("created_on"),
                        rs.getInt("belongs_device"),
                        rs.getString("ticket"));
            }

            return result;
        });
    }

    @Override
    public Principals getPrincipalsForTicket(String ticket) {
        return runSqlOperation(con -> {
            String getTicketQuery = "SELECT d.`ID` as did, d.name as dname, u.`ID` as uid, u.name as uname " +
                    "FROM Ticket t, User u, User_device d " +
                    "WHERE ticket=? AND t.belongs_device=d.`ID` AND d.belongs_to=u.`ID`";
            PreparedStatement sqlQuery = con.prepareStatement(getTicketQuery);
            sqlQuery.setString(1, ticket);

            Principals result = null;
            ResultSet rs = sqlQuery.executeQuery();
            while (rs.next()){
                result = new Principals(rs.getString("uname"),
                        rs.getString("dname"),
                        rs.getInt("uid"),
                        rs.getInt("did"));
            }

            return result;
        });
    }
}
