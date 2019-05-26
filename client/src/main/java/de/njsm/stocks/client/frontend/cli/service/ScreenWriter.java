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

package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.business.data.*;
import de.njsm.stocks.client.business.data.view.UserDeviceView;
import de.njsm.stocks.client.business.data.visitor.ToStringVisitor;

import java.io.PrintStream;
import java.util.List;

public class ScreenWriter {

    private PrintStream outputStream;

    private ToStringVisitor stringBuilder;

    public ScreenWriter(PrintStream outputStream,
                        ToStringVisitor stringBuilder) {
        this.outputStream = outputStream;
        this.stringBuilder = stringBuilder;
    }

    public void println(String text) {
        outputStream.println(text);
    }

    public void printFood(String headline, List<Food> foodList) {
        printDataList(headline, "food", foodList);
    }

    public void printLocations(String headline, List<Location> locations) {
        printDataList(headline, "locations", locations);
    }

    public void printUsers(String headline, List<User> users) {
        printDataList(headline, "users", users);
    }

    public void printUserDeviceViews(String headline, List<UserDeviceView> devices) {
        printDataList(headline, "devices", devices);
    }

    public void printData(Data input) {
        println(stringBuilder.visit(input, null));
    }

    public <T extends Data> void printDataList(String headline, String dataName, List<T> dataList) {
        if (dataList.isEmpty()) {
            println("No " + dataName + " there...");
        } else {
            println(headline);
            for (T item : dataList) {
                printData(item);
            }
        }
    }
}
