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

    private String[] resetCommands = {
            "DELETE FROM Food",
            "DELETE FROM Food_item",
            "DELETE FROM User",
            "DELETE FROM User_device",
            "DELETE FROM Location",
            "DELETE FROM Updates",

            "INSERT INTO Updates (`ID`, `table_name`, `last_update`) VALUES " +
                    "(1, 'Location', '1970-01-01 00:00:00.000')," +
                    "(2, 'User', '1970-01-01 00:00:00.000')," +
                    "(3, 'User_device', '1970-01-01 00:00:00.000')," +
                    "(4, 'Food', '1970-01-01 00:00:00.000')," +
                    "(5, 'Food_item', '1970-01-01 00:00:00.000')",

            "INSERT INTO User (`ID`, `name`) VALUES " +
                    "(1, 'John'), " +
                    "(2, 'Jack'), " +
                    "(3, 'Juliette') ",

            "INSERT INTO User_device (`ID`, `name`, `belongs_to`) VALUES " +
                    "(1, 'Mobile', 1), " +
                    "(2, 'Mobile', 2), " +
                    "(3, 'Mobile', 3), " +
                    "(4, 'Laptop', 1), " +
                    "(5, 'Desktop-PC', 1), " +
                    "(6, 'PC-Work', 2), " +
                    "(7, 'Laptop', 3)",

            "INSERT INTO Location (`ID`, `name`) VALUES " +
                    "(1, 'Fridge'), " +
                    "(2, 'Cupboard'), " +
                    "(3, 'Cupboard'), " +
                    "(4, 'Basement')",

            "INSERT INTO Food (`ID`, `name`) VALUES " +
                    "(1, 'Beer')," +
                    "(2, 'Carrot')," +
                    "(3, 'Bread')," +
                    "(4, 'Milk')," +
                    "(5, 'Yoghurt')," +
                    "(6, 'Raspberry jam')," +
                    "(7, 'Apple juice')",

            "INSERT INTO Food_item (`ID`, eat_by, of_type, stored_in, registers, buys) " +
                    "VALUES " +
                    "(1, '1970-01-01 00:00:00.000', 1, 1, 2, 2), " +
                    "(2, '1970-01-02 00:00:00.000', 1, 1, 2, 2), " +
                    "(3, '1970-01-03 00:00:00.000', 3, 2, 1, 1), " +
                    "(4, '1970-01-04 00:00:00.000', 4, 1, 1, 1), " +
                    "(5, '1970-01-05 00:00:00.000', 1, 1, 2, 2), " +
                    "(6, '1970-01-06 00:00:00.000', 1, 1, 2, 2), " +
                    "(7, '1970-01-07 00:00:00.000', 6, 3, 2, 2), " +
                    "(8, '1970-01-08 00:00:00.000', 7, 3, 3, 3), " +
                    "(9, '1970-01-09 00:00:00.000', 7, 4, 3, 3)"
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
