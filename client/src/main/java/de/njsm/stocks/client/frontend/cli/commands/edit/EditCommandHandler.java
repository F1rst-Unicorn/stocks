package de.njsm.stocks.client.frontend.cli.commands.edit;

import de.njsm.stocks.client.business.data.FoodItem;
import de.njsm.stocks.client.business.data.Location;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.FaultyCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;

import java.time.Instant;

public class EditCommandHandler extends FaultyCommandHandler {

    private final InputCollector inputCollector;

    private final ServerManager serverManager;

    private Refresher refresher;

    public EditCommandHandler(ServerManager serverManager,
                              InputCollector inputCollector,
                              ScreenWriter writer,
                              Refresher refresher) {
        super(writer);
        command = "edit";
        description = "Edit a food item to change location or due date";
        this.inputCollector = inputCollector;
        this.serverManager = serverManager;
        this.refresher = refresher;
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            printHelp();
        } else {
            handleWithFaultLogger(command);
        }
    }

    @Override
    public void printHelp() {
        String text = "Edit a food item to change location or due date\n" +
                "\t--f string\t\t\tfood: The food type to move\n" +
                "\t--l string\t\t\tlocation: Where to put the food\n" +
                "\t--d date  \t\t\tdate: Eat before this date\n";

        writer.println(text);
    }

    @Override
    protected void handleInternally(Command command) throws NetworkException, DatabaseException, InputException {
        FoodItem item = inputCollector.determineItem(command);
        Location location = inputCollector.determineDestinationLocation(command);
        Instant newEatByDate = inputCollector.determineDate(command, item.eatByDate);
        serverManager.edit(item, newEatByDate, location.id);
        refresher.refresh();
    }
}
