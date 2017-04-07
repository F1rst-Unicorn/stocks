package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;

import java.util.ArrayList;
import java.util.List;

public class LocationCommandHandler extends AbstractCommandHandler {

    protected final AggregatedCommandHandler m;

    private Selector selector;

    private Refresher refresher;

    public LocationCommandHandler(Configuration c, ScreenWriter writer, Selector selector, Refresher refresher) {
        super(writer);
        command = "loc";
        description = "Manage the locations to store food";
        this.c = c;

        List<AbstractCommandHandler> commands = new ArrayList<>();
        commands.add(new LocationAddCommandHandler(c, writer, refresher));
        commands.add(new LocationListCommandHandler(c, writer));
        commands.add(new LocationRenameCommandHandler(c, writer, selector, refresher));
        commands.add(new LocationRemoveCommandHandler(c, writer, selector, refresher));
        m = new AggregatedCommandHandler(writer, commands, command);
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            m.handle(command);
        } else {
            new LocationListCommandHandler(c, writer).handle(command);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }

}
