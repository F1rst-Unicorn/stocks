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
