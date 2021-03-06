/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.client.storage;

import de.njsm.stocks.client.config.Configuration;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
                    "(1, 'Location', '1970.01.01-00:00:00.000000-+0000'), " +
                    "(2, 'User', '1970.01.01-00:00:00.000000-+0000'), " +
                    "(3, 'User_device', '1970.01.01-00:00:00.000000-+0000'), " +
                    "(4, 'Food', '1970.01.01-00:00:00.000000-+0000'), " +
                    "(5, 'Food_item', '1970.01.01-00:00:00.000000-+0000')",

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
                    "(1, 6, '1970.01.01-00:00:00.000000-+0000', 1, 1, 2, 2), " +
                    "(2, 7, '1970.01.02-00:00:00.000000-+0000', 1, 1, 2, 2), " +
                    "(3, 8, '1970.01.03-00:00:00.000000-+0000', 3, 2, 1, 1), " +
                    "(4, 9, '1970.01.04-00:00:00.000000-+0000', 4, 1, 1, 1), " +
                    "(5, 10, '1970.01.05-00:00:00.000000-+0000', 1, 1, 2, 2), " +
                    "(6, 11, '1970.01.06-00:00:00.000000-+0000', 1, 1, 2, 2), " +
                    "(7, 12, '1970.01.07-00:00:00.000000-+0000', 6, 3, 2, 2), " +
                    "(8, 13, '1970.01.08-00:00:00.000000-+0000', 7, 3, 3, 3), " +
                    "(9, 14, '1970.01.09-00:00:00.000000-+0000', 7, 4, 3, 3)"
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
        String file = IOUtils.toString(is, StandardCharsets.UTF_8);
        is.close();

        String[] commands = file.split(";");
        List<String> script = new ArrayList<>(Arrays.asList(commands).subList(2, commands.length));
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
