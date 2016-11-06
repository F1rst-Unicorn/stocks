package de.njsm.stocks.sentry.db;

import de.njsm.stocks.sentry.auth.CertificateManager;
import de.njsm.stocks.sentry.data.Principals;

import java.io.File;
import java.sql.*;

public class DatabaseHandler {

    private final String url;
    private final int validityTime;

    public DatabaseHandler() throws ClassNotFoundException {

        Class.forName("com.mariadb.jdbc.Driver");

        Config c = new Config();
        validityTime = Integer.parseInt(c.getDbValidity());

        String address = c.getDbAddress();
        String port = c.getDbPort();
        String name = c.getDbName();
        String user = c.getDbUsername();
        String password = c.getDbPassword();

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
    public boolean isTicketValid(String ticket, int deviceId) throws SQLException {
        String query = "SELECT * FROM Ticket WHERE ticket=?";

        try (Connection con = getConnection();
             PreparedStatement sqlQuery = con.prepareStatement(query)){

            sqlQuery.setString(1, ticket);
            ResultSet rs = sqlQuery.executeQuery();

            boolean result = false;
            java.util.Date date = null;
            int storedId = -1;

            while (rs.next()){
                date = rs.getTimestamp("created_on");
                storedId = rs.getInt("belongs_device");
                result = true;
            }

            if (result) {
                java.util.Date valid_till_date = new Date(date.getTime() + validityTime * 60000);
                java.util.Date now = new java.util.Date();

                return now.before(valid_till_date) &&
                        storedId == deviceId;
            } else {
                return false;
            }

        }
    }

    public void handleTicket (String ticket, int deviceId) {

        Connection con = null;
        CertificateManager cm = new CertificateManager();
        String userFile = String.format("user_%d", deviceId);
        String csrFilePath = String.format(CertificateManager.csrFormatString, userFile);
        String certFilePath = String.format(CertificateManager.certFormatString, userFile);

        try {

            con = getConnection();
            con.setAutoCommit(false);

            // get ticket associated data
            boolean hasTicket = false;
            String dbUsername = null, dbDevicename = null;
            int dbUid = -1, dbDid = -1;
            String getTicketQuery = "SELECT d.`ID` as did, d.name as dname, u.`ID` as uid, u.name as uname " +
                    "FROM Ticket t, User u, User_device d " +
                    "WHERE ticket=? AND t.belongs_device=d.`ID` AND d.belongs_to=u.`ID`";
            PreparedStatement sqlQuery = con.prepareStatement(getTicketQuery);
            sqlQuery.setString(1, ticket);
            ResultSet rs = sqlQuery.executeQuery();
            while (rs.next()){
                dbUsername = rs.getString("uname");
                dbUid = rs.getInt("uid");
                dbDevicename = rs.getString("dname");
                dbDid = rs.getInt("did");
                hasTicket = true;
            }
            // get csr associated data
            Principals csrPrincipals = cm.getPrincipals(csrFilePath);
            Principals dbPrincipals = new Principals(dbUsername, dbDevicename, dbUid, dbDid);

            // check for equality, if not, exception
            if (! (hasTicket && csrPrincipals.equals(dbPrincipals))) {
                throw new Exception("CSR Subject name differs from database! " + dbPrincipals.toString() + " " +
                        csrPrincipals.toString());
            }

            cm.generateCertificate(userFile);

            // remove ticket
            String removeTicketCommand = "DELETE FROM Ticket WHERE belongs_device=?";
            PreparedStatement sqlRemoveTicketCommand = con.prepareStatement(removeTicketCommand);
            sqlRemoveTicketCommand.setInt(1, deviceId);
            sqlRemoveTicketCommand.execute();

            con.commit();
        } catch (Exception e) {
            try {
                (new File(csrFilePath)).delete();
                (new File(certFilePath)).delete();
                if (con != null) {
                    con.rollback();
                }
                throw new RuntimeException("Aborting transaction: " + e.getMessage());
            } catch (SQLException sql){
                throw new RuntimeException("Aborting transaction, failed to rollback! " + sql.getMessage());
            }
        }

    }

}
