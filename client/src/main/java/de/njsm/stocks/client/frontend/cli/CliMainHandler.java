package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.frontend.MainHandler;
import de.njsm.stocks.client.frontend.cli.commands.*;
import de.njsm.stocks.client.config.Configuration;

import java.text.ParseException;
import java.util.ArrayList;


public class CliMainHandler implements MainHandler {

    private final CommandManager m;
    private final Configuration c;

    CliMainHandler(Configuration c) {
        this.c = c;

        ArrayList<AbstractCommandHandler> commandHandler = new ArrayList<>();
        commandHandler.add(new AddCommandHandler(c));
        commandHandler.add(new MoveCommandHandler(c));
        commandHandler.add(new EatCommandHandler(c));
        commandHandler.add(new FoodCommandHandler(c));
        commandHandler.add(new LocationCommandHandler(c));
        commandHandler.add(new RefreshCommandHandler(c));
        commandHandler.add(new UserCommandHandler(c));
        commandHandler.add(new DeviceCommandHandler(c));

        m = new CommandManager(commandHandler);
    }

    @Override
    public void run(String[] args) {
        boolean endRequested = false;
        Command command;

        if (args.length > 0) {
            try {
                command = Command.createCommand(args);
                m.handleCommand(command);
            } catch (ParseException e) {
                // TODO Log
            }
        } else {
            while (!endRequested) {
                String input = c.getReader().next("stocks $ ");

                switch (input) {
                    case "quit":
                        endRequested = true;
                        break;
                    case "":
                        break;
                    default:
                        try {
                            command = Command.createCommand(input);
                            m.handleCommand(command);
                        } catch (ParseException e) {
                            // TODO Log
                        }
                }
            }
        }
        c.getReader().shutdown();
    }
}
