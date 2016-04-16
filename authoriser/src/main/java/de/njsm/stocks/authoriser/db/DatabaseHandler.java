package de.njsm.stocks.authoriser.db;

import java.sql.*;

public class DatabaseHandler {

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/stocks_dev?user=server&password=linux"
            );
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Look up the given ticket in the database. Deletes the ticket if it exists
     * @param ticket The ticket to check
     * @return true iff the ticket is valid
     */
    public boolean authoriseTicket(String ticket) throws SQLException {
        String query = "SELECT * FROM Ticket WHERE ticket=?";
        String command = "DELETE FROM Ticket WHERE ID=?";

        try (Connection con = getConnection();
             PreparedStatement sqlQuery = con.prepareStatement(query);
             PreparedStatement sqlCommand = con.prepareStatement(command)){

            sqlQuery.setString(1, ticket);
            ResultSet rs = sqlQuery.executeQuery();

            int id = 0;
            boolean result = false;
            java.util.Date date = null;
            java.util.Date valid_till_date = new Date((new java.util.Date()).getTime() + 10 * 60000);

            while (rs.next()){
                id = rs.getInt("ID");
                date = rs.getTimestamp("created_on");
                result = true;
            }
            if (result && date.before(valid_till_date)){
                sqlCommand.setInt(1, id);
                sqlCommand.execute();
            } else {
                return false;
            }
        }
        return false;
    }
}
