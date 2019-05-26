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

package de.njsm.stocks.client.config;


import java.io.*;
import java.util.Properties;

public class PropertiesFileHandlerImpl implements PropertiesFileHandler {

    @Override
    public void writePropertiesToFile(String fileName, Properties source) throws IOException {
        FileWriter targetFile = new FileWriter(fileName);
        BufferedWriter fileWriter = new BufferedWriter(targetFile);
        source.store(fileWriter, "stocks configuration");
        fileWriter.close();
    }

    @Override
    public Properties readProperties(String fileName) throws IOException {
        FileReader sourceFile = new FileReader(fileName);
        BufferedReader source = new BufferedReader(sourceFile);
        Properties p = new Properties();
        p.load(source);
        source.close();
        return p;
    }
}
