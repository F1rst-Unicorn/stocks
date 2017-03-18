package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.frontend.cli.InputReader;
import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.client.exceptions.SelectException;
import de.njsm.stocks.client.frontend.cli.EnhancedInputReader;

import java.util.ArrayList;
import java.util.List;

public class LocationCommandHandler extends CommandHandler {

    protected final CommandManager m;

    public LocationCommandHandler(Configuration c) {
        command = "loc";
        description = "Manage the locations to store food";
        this.c = c;

        List<CommandHandler> commands = new ArrayList<>();
        commands.add(new LocationAddCommandHandler(c));
        commands.add(new LocationListCommandHandler(c));
        commands.add(new LocationRenameCommandHandler(c));
        commands.add(new LocationRemoveCommandHandler(c));
        m = new CommandManager(commands, command);
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            m.handleCommand(command);
        } else {
            new LocationListCommandHandler(c).handle(command);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }

    public static int selectLocation(Location[] l, String name) throws SelectException {
        InputReader scanner = new EnhancedInputReader(System.in);
        int result;

        if (l.length == 1) {
            result = l[0].id;
        } else if (l.length == 0) {
            throw new SelectException("No such location found: " + name);
        } else {
            System.out.println("Several locations found");
            for (Location loc : l) {
                System.out.println("\t" + loc.id + ": " + loc.name);
            }
            result = scanner.nextInt("Choose one ", l[0].id);
        }
        return result;
    }
}
