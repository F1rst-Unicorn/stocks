package de.njsm.stocks.client.frontend.cli.commands.eat;

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

public class EatCommandHandler extends FaultyCommandHandler {

    private InputCollector inputCollector;

    private Refresher refresher;

    private ServerManager serverManager;

    public EatCommandHandler(ServerManager serverManager, ScreenWriter writer, InputCollector inputCollector, Refresher refresher) {
        super(writer);
        command = "eat";
        description = "Eat a food item";
        this.inputCollector = inputCollector;
        this.refresher = refresher;
        this.serverManager = serverManager;
    }

    @Override
    protected void handleInternally(Command c) throws NetworkException, DatabaseException, InputException {
        FoodItem item = inputCollector.determineNextItem(c);
        serverManager.removeItem(item);
        refresher.refresh();
    }


}
