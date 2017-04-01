package de.njsm.stocks.client.frontend.cli.commands.move;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.common.data.FoodItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoveCommandHandler extends AbstractCommandHandler {

    private static final Logger LOG = LogManager.getLogger(MoveCommandHandler.class);

    private final InputCollector inputCollector;

    private final ServerManager serverManager;

    protected String food;

    public MoveCommandHandler(ServerManager serverManager,
                              InputCollector inputCollector,
                              ScreenWriter writer) {
        super(writer);
        command = "move";
        description = "Move a food item to a different location";
        this.inputCollector = inputCollector;
        this.serverManager = serverManager;
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
                "\t--l string\t\t\tlocation: Where to put the food\n\n";

        System.out.print(text);
    }

    private void handleMoveCommand(Command command) {
        try {
            FoodItem item = inputCollector.createItem(command);
            int locationId = inputCollector.createLocationId(command);
            serverManager.move(item, locationId);
        } catch (NetworkException e) {
            logNetworkError(e);
        } catch (InputException e) {
            logInputError(e);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        }
    }
}
