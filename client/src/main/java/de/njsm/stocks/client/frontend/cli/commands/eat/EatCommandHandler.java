package de.njsm.stocks.client.frontend.cli.commands.eat;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.common.data.FoodItem;

public class EatCommandHandler extends AbstractCommandHandler {

    private InputCollector inputCollector;

    private Refresher refresher;

    private ServerManager serverManager;

    public EatCommandHandler(Configuration c, ServerManager serverManager, ScreenWriter writer, InputCollector inputCollector, Refresher refresher) {
        super(writer);
        command = "eat";
        description = "Eat a food item";
        this.c = c;
        this.inputCollector = inputCollector;
        this.refresher = refresher;
        this.serverManager = serverManager;
    }

    @Override
    public void handle(Command command) {
        try {
            handleInternally(command);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        } catch (InputException e) {
            logInputError(e);
        } catch (NetworkException e) {
            logNetworkError(e);
        }
    }

    private void handleInternally(Command c) throws NetworkException, DatabaseException, InputException {
        FoodItem item = inputCollector.resolveItem(c);
        serverManager.removeItem(item);
        refresher.refresh();
    }


}
