package de.njsm.stocks.linux.client.frontend.cli;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.frontend.MainHandler;
import de.njsm.stocks.linux.client.frontend.cli.commands.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class CliMainHandler implements MainHandler {

    protected final CommandManager m;
    protected final Configuration c;

    public CliMainHandler(Configuration c) {
        this.c = c;

        ArrayList<CommandHandler> commandHandler = new ArrayList<>();
        commandHandler.add(new AddCommandHandlerHandler(c));
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
                c.getLog().severe("Invalid command: " + e.getMessage());
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
                            c.getLog().severe("Invalid Command: " + e.getMessage());
                        }
                }
            }
        }
        c.getReader().shutdown();
    }

}
