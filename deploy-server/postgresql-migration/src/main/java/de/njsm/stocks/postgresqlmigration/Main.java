package de.njsm.stocks.postgresqlmigration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Map;
import java.util.Properties;

public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    private static final String CONFIG_FILE_KEY = "de.njsm.stocks.postgresqlmigration.configPath";

    private static final String POSTGRES_CONFIG_PREFIX = "de.njsm.stocks.server.v2.db.postgres.";

    public static void main(String[] args) {

        loadDbDrivers();
        Properties config = loadConfiguration();

        performMigration(config);
    }

    private static void performMigration(Properties config) {
        try {
            performMigrationInternally(config);

        } catch (SQLException e) {
            LOG.error("Migration failed", e);
            System.exit(3);
        }
    }

    private static void performMigrationInternally(Properties config) throws SQLException {
        Connection source = setupSourceConnection(config);
        Connection destination = setupDestinationConnection(config);

        copyData(source, destination);
        setupSequences(destination);

        closeConnection(source);
        closeConnection(destination);
    }

    private static void closeConnection(Connection connection) throws SQLException {
        connection.commit();
        connection.close();
    }

    private static void copyData(Connection source, Connection destination) throws SQLException {
        String[] simpleTables = {
                "Food",
                "Location",
                "User"
        };
        ResultSet rs;
        PreparedStatement stmt;

        for (String table : simpleTables) {
            rs = source.createStatement().executeQuery("select * from " + table);
            stmt = destination.prepareStatement("insert into \"" + table + "\" (\"ID\", \"name\", \"version\") values (?,?,?)");
            while (rs.next()) {
                stmt.setInt(1, rs.getInt("ID"));
                stmt.setString(2, rs.getString("name"));
                stmt.setInt(3, rs.getInt("version"));
                stmt.execute();
            }
            rs.close();
            stmt.close();
        }

        rs = source.createStatement().executeQuery("select * from User_device");
        stmt = destination.prepareStatement("insert into \"User_device\" (\"ID\", \"name\", \"version\", \"belongs_to\") values (?,?,?,?)");
        while (rs.next()) {
            stmt.setInt(1, rs.getInt("ID"));
            stmt.setString(2, rs.getString("name"));
            stmt.setInt(3, rs.getInt("version"));
            stmt.setInt(4, rs.getInt("belongs_to"));
            stmt.execute();
        }
        rs.close();
        stmt.close();

        rs = source.createStatement().executeQuery("select * from Food_item");
        stmt = destination.prepareStatement("insert into \"Food_item\" (\"ID\", \"eat_by\", \"of_type\", \"stored_in\", \"registers\", \"buys\", \"version\") values (?,?,?,?,?,?,?)");
        while (rs.next()) {
            stmt.setInt(1, rs.getInt("ID"));
            stmt.setTimestamp(2, rs.getTimestamp("eat_by"));
            stmt.setInt(3, rs.getInt("of_type"));
            stmt.setInt(4, rs.getInt("stored_in"));
            stmt.setInt(5, rs.getInt("registers"));
            stmt.setInt(6, rs.getInt("buys"));
            stmt.setInt(7, rs.getInt("version"));
            stmt.execute();
        }
        rs.close();
        stmt.close();

        rs = source.createStatement().executeQuery("select * from Ticket");
        stmt = destination.prepareStatement("insert into \"Ticket\" (\"ID\", \"ticket\", \"belongs_device\", \"created_on\") values (?,?,?,?)");
        while (rs.next()) {
            stmt.setInt(1, rs.getInt("ID"));
            stmt.setString(2, rs.getString("ticket"));
            stmt.setInt(3, rs.getInt("belongs_device"));
            stmt.setInt(4, rs.getInt("created_on"));
            stmt.execute();
        }
        rs.close();
        stmt.close();

        rs = source.createStatement().executeQuery("select * from EAN_number");
        stmt = destination.prepareStatement("insert into \"EAN_number\" (\"ID\", \"number\", \"identifies\", \"version\") values (?,?,?,?)");
        while (rs.next()) {
            stmt.setInt(1, rs.getInt("ID"));
            stmt.setString(2, rs.getString("number"));
            stmt.setInt(3, rs.getInt("identifies"));
            stmt.setInt(4, rs.getInt("version"));
            stmt.execute();
        }
        rs.close();
        stmt.close();
    }

    private static void setupSequences(Connection connection) throws SQLException {
        String[] tables = {
                "EAN_number",
                "Food",
                "Food_item",
                "Location",
                "User",
                "User_device"
        };

        Statement stmt = connection.createStatement();

        for (String table: tables) {
            ResultSet rs = stmt.executeQuery("select max(\"ID\") as max from \"" + table + "\"");
            rs.next();
            int sequenceValue = rs.getInt("max") + 1;
            stmt.execute("alter sequence \"" + table + "_ID_seq\" restart with " + sequenceValue);
            rs.close();
        }
        stmt.close();
    }

    private static Connection setupDestinationConnection(Properties config) throws SQLException {
        Properties postgresSettings = new Properties();
        String url = String.format("jdbc:postgresql://%s:%s/%s",
                config.getProperty("de.njsm.stocks.server.v2.db.host"),
                config.getProperty("de.njsm.stocks.server.v2.db.port"),
                config.getProperty("de.njsm.stocks.server.v2.db.name"));

        for (Map.Entry<Object, Object> entry: config.entrySet()) {
            if (entry.getKey() instanceof String) {
                String key = (String) entry.getKey();
                if (key.startsWith(POSTGRES_CONFIG_PREFIX)) {
                    postgresSettings.put(
                            key.replace(POSTGRES_CONFIG_PREFIX, ""),
                            entry.getValue()
                    );
                }
            }
        }

        Connection connection = DriverManager.getConnection(url, postgresSettings);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        connection.setAutoCommit(false);

        return connection;
    }

    private static Connection setupSourceConnection(Properties config) throws SQLException {
        String sourceUrl = String.format("jdbc:mariadb://%s:%s/%s?useLegacyDatetimeCode=false&serverTimezone=+00:00",
                config.getProperty("de.njsm.stocks.internal.db.databaseAddress"),
                config.getProperty("de.njsm.stocks.internal.db.databasePort"),
                config.getProperty("de.njsm.stocks.internal.db.databaseName"));

        Connection source = DriverManager.getConnection(sourceUrl,
                config.getProperty("de.njsm.stocks.internal.db.databaseUsername"),
                config.getProperty("de.njsm.stocks.internal.db.databasePassword"));
        source.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        source.setAutoCommit(false);

        return source;
    }

    private static Properties loadConfiguration() {
        Properties config = new Properties();
        try {
            InputStream stream = new FileInputStream(System.getProperty(CONFIG_FILE_KEY));
            config.load(stream);
            stream.close();
        } catch (IOException e) {
            LOG.error("Config file couldn't be loaded", e);
            System.exit(2);
        }
        return config;
    }

    private static void loadDbDrivers() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOG.error("DB driver not present", e);
            System.exit(1);
        }
    }
}
