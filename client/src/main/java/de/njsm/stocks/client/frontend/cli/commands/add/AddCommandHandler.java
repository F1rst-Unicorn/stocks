package de.njsm.stocks.client.frontend.cli.commands.add;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.RefreshCommandHandler;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.common.data.FoodItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddCommandHandler extends AbstractCommandHandler {

    private static final Logger LOG = LogManager.getLogger(AddCommandHandler.class);

    private final InputCollector inputCollector;

    private final ServerManager serverManager;

    private final RefreshCommandHandler refreshCommandHandler;

    public AddCommandHandler(InputCollector inputCollector,
                             ServerManager serverManager,
                             RefreshCommandHandler refreshCommandHandler,
                             ScreenWriter writer) {
        super(writer);
        command = "add";
        description = "Add a food item";
        this.inputCollector = inputCollector;
        this.refreshCommandHandler = refreshCommandHandler;
        this.serverManager = serverManager;
    }

    @Override
    public void handle(Command command) {
        LOG.info("Handling add command: " + command.toString());
        if (command.hasNext()) {
            printHelp();
        } else {
            handleAddCommand(command);
        }
    }

    @Override
    public void printHelp() {
        String text = "Add food item to the store\n" +
                "\t--f string\t\t\tfood: What to add" +
                "\t--d date  \t\t\tdate: Eat before this date\n" +
                "\t--l string\t\t\tlocation: Where to put the food\n\n";

        writer.println(text);
    }

    private void handleAddCommand(Command command) {
        try {
            FoodItem item = inputCollector.createFoodItem(command);
            serverManager.addItem(item);
            refreshCommandHandler.refresh();
        } catch (NetworkException e) {
            logNetworkError(e);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        } catch (InputException e) {
            logInputError(e);
        }
    }
}
