package de.njsm.stocks.client.frontend.cli.commands.move;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.common.data.FoodItem;

public class MoveCommandHandler extends AbstractCommandHandler {

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
            handleMoveCommand(command);
        }
    }

    @Override
    public void printHelp() {
        String text = "Move a food item to a different location\n" +
                "\t--f string\t\t\tfood: The food type to move\n" +
                "\t--l string\t\t\tlocation: Where to put the food\n";

        writer.println(text);
    }

    private void handleMoveCommand(Command command) {
        try {
            FoodItem item = inputCollector.createItem(command);
            int locationId = inputCollector.createLocationId(command);
            serverManager.move(item, locationId);
            refresher.refresh();
        } catch (NetworkException e) {
            logNetworkError(e);
        } catch (InputException e) {
            logInputError(e);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        }
    }
}
