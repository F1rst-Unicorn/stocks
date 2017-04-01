package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.frontend.cli.*;
import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.client.exceptions.InputException;

import java.util.ArrayList;
import java.util.List;

public class LocationCommandHandler extends AbstractCommandHandler {

    protected final CommandManager m;

    public LocationCommandHandler(Configuration c, ScreenWriter writer) {
        super(writer);
        command = "loc";
        description = "Manage the locations to store food";
        this.c = c;

        List<AbstractCommandHandler> commands = new ArrayList<>();
        commands.add(new LocationAddCommandHandler(c, writer));
        commands.add(new LocationListCommandHandler(c, writer));
        commands.add(new LocationRenameCommandHandler(c, writer));
        commands.add(new LocationRemoveCommandHandler(c, writer));
        m = new CommandManager(commands, command);
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            m.handleCommand(command);
        } else {
            new LocationListCommandHandler(c, writer).handle(command);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }

    public static int selectLocation(List<Location> l, String name) throws InputException {
        InputReader scanner = new InputReader(System.in);
        int result;

        if (l.size() == 1) {
            result = l.get(0).id;
        } else if (l.size() == 0) {
            throw new InputException("No such location found: " + name);
        } else {
            System.out.println("Several locations found");
            for (Location loc : l) {
                System.out.println("\t" + loc.id + ": " + loc.name);
            }
            result = scanner.nextInt("Choose one ", l.get(0).id);
        }
        return result;
    }
}
