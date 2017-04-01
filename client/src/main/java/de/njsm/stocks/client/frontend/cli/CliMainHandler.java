package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.ParseException;
import de.njsm.stocks.client.frontend.MainHandler;
import de.njsm.stocks.client.frontend.cli.commands.*;
import de.njsm.stocks.client.frontend.cli.commands.add.AddCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.add.InputCollector;
import de.njsm.stocks.client.frontend.cli.commands.move.MoveCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.client.storage.DatabaseManager;

import java.util.ArrayList;


public class CliMainHandler implements MainHandler {

    private final CommandManager m;
    private final Configuration c;

    CliMainHandler(Configuration c) {
        this.c = c;

        ScreenWriter writer = new ScreenWriter(System.out);
        InputReader reader = new InputReader(System.in);
        Selector selector = new Selector(writer, reader);
        DatabaseManager dbManager = new DatabaseManager();

        InputCollector addCollector = new InputCollector(
                dbManager,
                reader,
                writer);
        de.njsm.stocks.client.frontend.cli.commands.move.InputCollector moveCollector =
                new de.njsm.stocks.client.frontend.cli.commands.move.InputCollector(
                        dbManager,
                        selector,
                        writer,
                        reader);

        ArrayList<AbstractCommandHandler> commandHandler = new ArrayList<>();
        commandHandler.add(new AddCommandHandler(addCollector,
                c.getServerManager(),
                new RefreshCommandHandler(c, writer, false),
                writer));
        commandHandler.add(new MoveCommandHandler(c.getServerManager(),
                moveCollector, writer));
        commandHandler.add(new EatCommandHandler(c, writer));
        commandHandler.add(new FoodCommandHandler(c, writer));
        commandHandler.add(new LocationCommandHandler(c, writer));
        commandHandler.add(new RefreshCommandHandler(c, writer));
        commandHandler.add(new UserCommandHandler(c, writer));
        commandHandler.add(new DeviceCommandHandler(c, writer));

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
