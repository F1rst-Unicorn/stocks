package de.njsm.stocks.client.storage;

import de.njsm.stocks.client.config.Configuration;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper {

    private static final String DB_SCHEMA = System.getProperty("sql.schema.path");

    private Connection dbConnection;

    // Version = ID + 5
    private String[] resetCommands = {
            "DELETE FROM Food",
            "DELETE FROM Food_item",
            "DELETE FROM User",
            "DELETE FROM User_device",
            "DELETE FROM Location",
            "DELETE FROM Updates",

            "INSERT INTO Updates (`ID`, `table_name`, `last_update`) VALUES " +
                    "(1, 'Location', 0), " +
                    "(2, 'User', 0), " +
                    "(3, 'User_device', 0), " +
                    "(4, 'Food', 0), " +
                    "(5, 'Food_item', 0)",

            "INSERT INTO User (`ID`, `version`, `name`) VALUES " +
                    "(1, 6, 'John'), " +
                    "(2, 7, 'Jack'), " +
                    "(3, 8, 'Juliette') ",

            "INSERT INTO User_device (`ID`, `version`, `name`, `belongs_to`) VALUES " +
                    "(1, 6, 'Mobile', 1), " +
                    "(2, 7, 'Mobile', 2), " +
                    "(3, 8, 'Mobile', 3), " +
                    "(4, 9, 'Laptop', 1), " +
                    "(5, 10, 'Desktop-PC', 1), " +
                    "(6, 11, 'PC-Work', 2), " +
                    "(7, 12, 'Laptop', 3)",

            "INSERT INTO Location (`ID`, `version`, `name`) VALUES " +
                    "(1, 6, 'Fridge'), " +
                    "(2, 7, 'Cupboard'), " +
                    "(3, 8, 'Cupboard'), " +
                    "(4, 9, 'Basement')",

            "INSERT INTO Food (`ID`, `version`, `name`) VALUES " +
                    "(1, 6, 'Beer')," +
                    "(2, 7, 'Carrot')," +
                    "(3, 8, 'Bread')," +
                    "(4, 9, 'Milk')," +
                    "(5, 10, 'Yoghurt')," +
                    "(6, 11, 'Raspberry jam')," +
                    "(7, 12, 'Apple juice')",

            "INSERT INTO Food_item (`ID`, `version`, eat_by, of_type, stored_in, registers, buys) " +
                    "VALUES " +
                    "(1, 6, 0, 1, 1, 2, 2), " +
                    "(2, 7, 86400000, 1, 1, 2, 2), " +
                    "(3, 8, 172800000, 3, 2, 1, 1), " +
                    "(4, 9, 259200000, 4, 1, 1, 1), " +
                    "(5, 10, 345600000, 1, 1, 2, 2), " +
                    "(6, 11, 432000000, 1, 1, 2, 2), " +
                    "(7, 12, 518400000, 6, 3, 2, 2), " +
                    "(8, 13, 604800000, 7, 3, 3, 3), " +
                    "(9, 14, 691200000, 7, 4, 3, 3)"
    };

    public void setupDatabase() throws SQLException, IOException {
        createFile();
        sourceSchema();
    }

    public void fillData() throws SQLException {
        dbConnection = openConnection();
        runSqlScript(Arrays.asList(resetCommands));
        dbConnection.close();
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + Configuration.DB_PATH);
    }

    public void removeDatabase() throws SQLException {
        (new File(Configuration.DB_PATH)).delete();
    }

    public void runSqlCommand(String command) throws Exception {
        dbConnection = openConnection();
        Statement statement = dbConnection.createStatement();
        statement.execute(command);
        dbConnection.close();
    }

    private void createFile() throws SQLException, IOException {
        File dbFile = new File(Configuration.DB_PATH);
        dbFile.getParentFile().mkdirs();
        dbConnection = openConnection();
    }

    private void sourceSchema() throws IOException, SQLException {
        FileInputStream is = new FileInputStream(DB_SCHEMA);
        String file = IOUtils.toString(is);
        is.close();

        List<String> script = Arrays.asList(file.split(";"));
        script.set(script.size() - 1, "DELETE FROM Food");
        runSqlScript(script);
        dbConnection.close();
    }

    private void runSqlScript(List<String> script) throws SQLException {
        dbConnection.setAutoCommit(false);
        Statement statement = dbConnection.createStatement();
        for (String command : script) {
            statement.execute(command);
            statement.close();
        }
        dbConnection.commit();
        dbConnection.setAutoCommit(true);
    }
}
