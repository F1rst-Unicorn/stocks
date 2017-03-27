package de.njsm.stocks.client.frontend.cli.commands.add;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.RefreshCommandHandler;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.common.data.FoodItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddCommandHandler extends AbstractCommandHandler {

    private static final Logger LOG = LogManager.getLogger(AddCommandHandler.class);

    private InputCollector inputCollector;

    private ServerManager serverManager;

    private RefreshCommandHandler refreshCommandHandler;

    private ScreenWriter writer;

    public AddCommandHandler(InputCollector inputCollector,
                             ServerManager serverManager,
                             RefreshCommandHandler refreshCommandHandler,
                             ScreenWriter writer) {
        command = "add";
        description = "Add a food item";
        this.inputCollector = inputCollector;
        this.refreshCommandHandler = refreshCommandHandler;
        this.serverManager = serverManager;
        this.writer = writer;
    }

    @Override
    public void handle(Command command) {
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
            writer.println("There is a problem with the server connection");
            LOG.error("Network action failed", e);
        } catch (DatabaseException e) {
            writer.println("There is a problem with the local stocks copy");
            LOG.error("Database action failed", e);
        } catch (InputException e) {
            writer.println("The command cannot executed with this input");
            LOG.error("Problem with the input", e);
        }
    }
}
