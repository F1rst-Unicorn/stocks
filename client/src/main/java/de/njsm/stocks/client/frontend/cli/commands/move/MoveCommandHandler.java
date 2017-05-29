package de.njsm.stocks.client.frontend.cli.commands.move;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.FaultyCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Location;

public class MoveCommandHandler extends FaultyCommandHandler {

    private final InputCollector inputCollector;

    private final ServerManager serverManager;

    private Refresher refresher;

    public MoveCommandHandler(ServerManager serverManager,
                              InputCollector inputCollector,
                              ScreenWriter writer,
                              Refresher refresher) {
        super(writer);
        command = "move";
        description = "Move a food item to a different location";
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
        String text = "Move a food item to a different location\n" +
                "\t--f string\t\t\tfood: The food type to move\n" +
                "\t--l string\t\t\tlocation: Where to put the food\n";

        writer.println(text);
    }

    @Override
    protected void handleInternally(Command command) throws NetworkException, DatabaseException, InputException {
        FoodItem item = inputCollector.determineItem(command);
        Location location = inputCollector.determineDestinationLocation(command);
        serverManager.move(item, location.id);
        refresher.refresh();
    }
}
