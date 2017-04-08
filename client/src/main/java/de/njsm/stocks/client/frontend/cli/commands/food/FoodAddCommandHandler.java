package de.njsm.stocks.client.frontend.cli.commands.food;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.common.data.Food;

public class FoodAddCommandHandler extends AbstractCommandHandler {

    private InputCollector inputCollector;

    private Refresher refresher;

    private ServerManager serverManager;

    public FoodAddCommandHandler(ScreenWriter writer, Refresher refresher, InputCollector inputCollector, ServerManager serverManager) {
        super(writer);
        this.command = "add";
        this.description = "Add a new food type";
        this.refresher = refresher;
        this.inputCollector = inputCollector;
        this.serverManager = serverManager;
    }

    @Override
    public void handle(Command command) {
        try {
            Food food = inputCollector.resolveNewFood(command);
            serverManager.addFood(food);
            refresher.refresh();
        } catch (NetworkException e) {
            logNetworkError(e);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        }
    }
}