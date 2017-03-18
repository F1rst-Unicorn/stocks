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
                    "(1, 'Location', '1970-01-01 01:00:00.000')," +
                    "(2, 'User', '1970-01-01 01:00:00.000')," +
                    "(3, 'User_device', '1970-01-01 01:00:00.000')," +
                    "(4, 'Food', '1970-01-01 01:00:00.000')," +
                    "(5, 'Food_item', '1970-01-01 01:00:00.000')",

            "INSERT INTO User (`ID`, `name`) VALUES " +
                    "(1, 'John'), " +
                    "(2, 'Jack'), " +
                    "(3, 'Juliette') "
    };

    void setupDatabase() throws SQLException, IOException {
        createFile();
        sourceSchema();
    }

    void fillData() throws SQLException {
        dbConnection = DriverManager.getConnection("jdbc:sqlite:" + Configuration.DB_PATH);
        runSqlScript(Arrays.asList(resetCommands));
        dbConnection.close();
    }

    void removeDatabase() throws SQLException {
        (new File(Configuration.DB_PATH)).delete();
    }

    private void createFile() throws SQLException, IOException {
        File dbFile = new File(Configuration.DB_PATH);
        dbFile.getParentFile().mkdirs();
        dbConnection = DriverManager.getConnection("jdbc:sqlite:" + Configuration.DB_PATH);
    }

    private void sourceSchema() throws IOException, SQLException {
        FileInputStream is = new FileInputStream(DB_SCHEMA);
        String file = IOUtils.toString(is);
        List<String> script = Arrays.asList(file.split(";"));
        script.set(script.size() - 1, "DELETE FROM Food");
        runSqlScript(script);
        is.close();
        dbConnection.close();
    }

    private void runSqlScript(List<String> script) throws SQLException {
        for (String command : script) {
            Statement statement = dbConnection.createStatement();
            statement.execute(command);
            statement.close();
        }
    }
}
