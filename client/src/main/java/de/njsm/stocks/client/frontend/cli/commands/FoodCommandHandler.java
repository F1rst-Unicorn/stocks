package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;

import java.util.ArrayList;
import java.util.List;

public class FoodCommandHandler extends AbstractCommandHandler {

    protected final AggregatedCommandHandler m;

    public FoodCommandHandler(Configuration c,
                              ScreenWriter writer, Selector selector, Refresher refresher) {
        super(writer);
        command = "food";
        description = "Manage the food types";
        this.c = c;

        List<AbstractCommandHandler> commands = new ArrayList<>();
        commands.add(new FoodAddCommandHandler(c, writer, refresher));
        commands.add(new FoodListCommandHandler(writer, c));
        commands.add(new FoodRenameCommandHandler(c, writer, selector, refresher));
        commands.add(new FoodRemoveCommandHandler(c, writer, selector, refresher));
        m = new AggregatedCommandHandler(writer, commands, command);
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            m.handle(command);
        } else {
            new FoodListCommandHandler(writer, c).handle(command);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }

}
