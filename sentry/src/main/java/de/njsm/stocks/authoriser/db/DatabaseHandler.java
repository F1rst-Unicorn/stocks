package de.njsm.stocks.authoriser.db;

import java.sql.*;

public class DatabaseHandler {

    private Connection getConnection() throws SQLException {
            return DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/stocks_dev?user=server&password=linux"
            );

    }

    /**
     * Look up the given ticket in the database. Deletes the ticket if it exists
     * @param ticket The ticket to check
     * @return true iff the ticket is valid
     */
    public boolean authoriseTicket(String[] ticket) throws SQLException {
        String query = "SELECT * FROM Ticket WHERE ticket=?";
        String command = "DELETE FROM Ticket WHERE ID=?";

        if (! validUsername(ticket)){
            return false;
        }

        try (Connection con = getConnection();
             PreparedStatement sqlQuery = con.prepareStatement(query);
             PreparedStatement sqlCommand = con.prepareStatement(command)){

            sqlQuery.setString(1, ticket[0]);
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

    public void addUser(String[] ticket){
        // TODO create new user and device in the database
    }

    public boolean validUsername(String[] ticket) throws SQLException {
        String userQuery = "SELECT * FROM User WHERE name=?";
        String deviceQuery = "SELECT * FROM User_device WHERE name=?";

        boolean userPresent = false;
        boolean devicePresent = false;

        try (Connection con = getConnection();
             PreparedStatement sqlUserQuery = con.prepareStatement(userQuery);
             PreparedStatement sqlDeviceQuery = con.prepareStatement(deviceQuery)){

            sqlUserQuery.setString(1, ticket[1]);
            ResultSet res = sqlUserQuery.executeQuery();
            while (res.next()){
                userPresent = true;
            }
            sqlDeviceQuery.setString(1, ticket[2]);
            res = sqlDeviceQuery.executeQuery();
            while (res.next()){
                devicePresent = true;
            }

            return !userPresent && ! devicePresent;
        }
    }
}
