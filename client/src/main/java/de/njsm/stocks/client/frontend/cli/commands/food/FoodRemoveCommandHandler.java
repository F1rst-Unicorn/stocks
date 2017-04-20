package de.njsm.stocks.client.frontend.cli.commands.food;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.common.data.Food;

public class FoodRemoveCommandHandler extends AbstractCommandHandler {

    private InputCollector inputCollector;

    private Refresher refresher;

    private ServerManager serverManager;

    public FoodRemoveCommandHandler(ScreenWriter writer, InputCollector inputCollector, Refresher refresher, ServerManager serverManager) {
        super(writer);
        this.command = "remove";
        this.description = "Remove food from the system";
        this.inputCollector = inputCollector;
        this.refresher = refresher;
        this.serverManager = serverManager;
    }

    @Override
    public void handle(Command command) {
        try {
            Food food = inputCollector.resolveFood(command);
            serverManager.removeFood(food);
            refresher.refresh();
        } catch (NetworkException e) {
            logNetworkError(e);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        } catch (InputException e) {
            logInputError(e);
        }
    }
}
