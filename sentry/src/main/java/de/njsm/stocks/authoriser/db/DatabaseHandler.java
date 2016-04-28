package de.njsm.stocks.authoriser.db;

import java.sql.*;

public class DatabaseHandler {

    protected String url;

    public DatabaseHandler() {

        String address = System.getProperty("de.njsm.stocks.internal.db.databaseAddress");
        String port = System.getProperty("de.njsm.stocks.internal.db.databasePort");
        String name = System.getProperty("de.njsm.stocks.internal.db.databaseName");
        String user = System.getProperty("de.njsm.stocks.internal.db.databaseUsername");
        String password = System.getProperty("de.njsm.stocks.internal.db.databasePassword");

        url = String.format("jdbc:mariadb://%s:%s/%s?user=%s&password=%s",
                address,
                port,
                name,
                user,
                password);

    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /**
     * Determine whether the ticket has been created
     * by an existing user
     *
     * @param ticket The ticket to check for
     * @return true iff the ticket is valid
     * @throws SQLException
     */
    public boolean isTicketValid(String ticket) throws SQLException {
        String query = "SELECT * FROM Ticket WHERE ticket=?";
        int minutesValid = Integer.parseInt(
                System.getProperty("de.njsm.stocks.internal.ticketValidityTimeInMinutes"));

        try (Connection con = getConnection();
             PreparedStatement sqlQuery = con.prepareStatement(query)){

            sqlQuery.setString(1, ticket);
            ResultSet rs = sqlQuery.executeQuery();

            boolean result = false;
            java.util.Date date = null;

            while (rs.next()){
                date = rs.getTimestamp("created_on");
                result = true;
            }
            java.util.Date valid_till_date = new Date(date.getTime() + minutesValid * 60000);

            return result && (new java.util.Date()).before(valid_till_date);

        }
    }

    /**
     * Get the ID of the ticket
     * @return The ID of the ticket
     * @throws SQLException
     */
    public int getCounter(String ticket) throws SQLException {
        String query = "SELECT `ID` " +
                "FROM  Ticket " +
                "WHERE ticket=?;";

        try (Connection con = getConnection();
             PreparedStatement sqlQuery = con.prepareStatement(query)){

            sqlQuery.setString(1, ticket);
            ResultSet rs = sqlQuery.executeQuery();

            while (rs.next()){
                return rs.getInt("ID");
            }
            return -1;
        }
    }

    /**
     * Adds the new device to the database. If the user already exists,
     * the device is linked to him, otherwise a new user is added, too.
     * @param credentials
     * @throws SQLException
     */
    public void addUser(String[] credentials, int certId) throws SQLException {
        String userQuery = "SELECT * FROM User WHERE name=?";
        String userCommand = "INSERT INTO User (name) VALUES (?)";
        String deviceCommand = "INSERT INTO User_device (name, belongs_to, certificate_no)" +
                " VALUES (?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement sqlUserQuery = con.prepareStatement(userQuery);
             PreparedStatement sqlUserCommand = con.prepareStatement(userCommand);
             PreparedStatement sqlDeviceCommand = con.prepareCall(deviceCommand)) {

            con.setAutoCommit(false);

            boolean userPresent = false;
            int userId = -1;
            sqlUserQuery.setString(1, credentials[0]);
            ResultSet res = sqlUserQuery.executeQuery();
            while (res.next()) {
                userPresent = true;
                userId = res.getInt("ID");
            }

            if (! userPresent) {
                sqlUserCommand.setString(1, credentials[0]);
                sqlUserCommand.execute();
                res = sqlUserQuery.executeQuery();
                while (res.next()) {
                    userId = res.getInt("ID");
                }
            }

            sqlDeviceCommand.setString(1, credentials[2]);
            sqlDeviceCommand.setInt(2, userId);
            sqlDeviceCommand.setInt(3, certId);
            sqlDeviceCommand.execute();

            con.commit();
        }
    }

    /**
     * Checks whether the Ids presented in the CSR match
     * the auto increment values in the database
     *
     * @param credentials the array of credentials learned from the CSR
     * @return true if the credentials are valid
     * @throws SQLException
     */
    public boolean isNameValid(String[] credentials) throws SQLException {

        String dbName = System.getProperty("de.njsm.stocks.internal.db.databaseName");
        String userIdQuery = "SELECT `AUTO_INCREMENT` " +
                "FROM  INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_SCHEMA = ? " +
                "AND   TABLE_NAME   = 'User';";

        String deviceIdQuery = "SELECT `AUTO_INCREMENT` " +
                "FROM  INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_SCHEMA = ? " +
                "AND   TABLE_NAME   = 'User_device';";

        boolean userIdValid = false;
        boolean deviceIdValid = false;

        try (Connection con = getConnection();
             PreparedStatement sqlUserIdQuery = con.prepareStatement(userIdQuery);
             PreparedStatement sqlDeviceIdQuery = con.prepareStatement(deviceIdQuery)){

            sqlUserIdQuery.setString(1, dbName);
            ResultSet res = sqlUserIdQuery.executeQuery();
            while (res.next()){
                userIdValid = res.getInt("AUTO_INCREMENT") == Integer.parseInt(credentials[1]);
            }

            sqlDeviceIdQuery.setString(1, dbName);
            res = sqlDeviceIdQuery.executeQuery();
            while (res.next()){
                deviceIdValid = res.getInt("AUTO_INCREMENT") == Integer.parseInt(credentials[3]);;
            }

            return userIdValid && deviceIdValid;
        }
    }

    public void removeTicket(int id) throws SQLException {
        String command = "DELETE FROM Ticket WHERE ID=?";

        try (Connection con = getConnection();
             PreparedStatement sqlCommand = con.prepareStatement(command)){

            sqlCommand.setInt(1, id);
            sqlCommand.execute();
        }
    }
}
