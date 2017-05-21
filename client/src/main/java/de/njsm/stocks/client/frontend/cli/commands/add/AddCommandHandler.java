package de.njsm.stocks.client.frontend.cli.commands.add;

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

public class AddCommandHandler extends FaultyCommandHandler {

    private final InputCollector inputCollector;

    private final ServerManager serverManager;

    private final Refresher refresher;

    public AddCommandHandler(InputCollector inputCollector,
                             ServerManager serverManager,
                             Refresher refresher,
                             ScreenWriter writer) {
        super(writer);
        command = "add";
        description = "Add a food item";
        this.inputCollector = inputCollector;
        this.refresher = refresher;
        this.serverManager = serverManager;
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
        String text = "Add food item to the store\n" +
                "\t--f string\t\t\tfood: What to add" +
                "\t--d date  \t\t\tdate: Eat before this date\n" +
                "\t--l string\t\t\tlocation: Where to put the food\n\n";

        writer.println(text);
    }

    @Override
    protected void handleInternally(Command command) throws DatabaseException, InputException, NetworkException {
        FoodItem item = inputCollector.createFoodItem(command);
        serverManager.addItem(item);
        refresher.refresh();
    }
}
