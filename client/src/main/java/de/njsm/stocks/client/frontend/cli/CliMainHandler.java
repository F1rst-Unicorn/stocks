package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.ParseException;
import de.njsm.stocks.client.frontend.MainHandler;
import de.njsm.stocks.client.frontend.cli.commands.*;
import de.njsm.stocks.client.frontend.cli.commands.add.AddCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.add.InputCollector;
import de.njsm.stocks.client.frontend.cli.commands.move.MoveCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.refresh.RefreshCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.client.storage.DatabaseManager;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.ArrayList;


public class CliMainHandler implements MainHandler {

    private final CommandManager m;
    private final Configuration c;

    CliMainHandler(Configuration c) {
        this.c = c;

        ScreenWriter writer = new ScreenWriter(System.out);
        InputReader reader = null;
        try {
            reader = new InputReader(new ConsoleReader(System.in, System.out), System.out);
        } catch (IOException e) {
            writer.println("Could not initialise prompt");
            System.exit(1);
        }
        Selector selector = new Selector(writer, reader);
        DatabaseManager dbManager = new DatabaseManager();
        Refresher refresher = new Refresher(c.getServerManager(), dbManager);

        InputCollector addCollector = new InputCollector(
                dbManager,
                selector,
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
                refresher,
                writer));
        commandHandler.add(new MoveCommandHandler(c.getServerManager(),
                moveCollector, writer, refresher));
        commandHandler.add(new EatCommandHandler(c, writer, selector, refresher));
        commandHandler.add(new FoodCommandHandler(c, writer, selector, refresher));
        commandHandler.add(new LocationCommandHandler(c, writer, selector, refresher));
        commandHandler.add(new RefreshCommandHandler(writer, refresher));
        commandHandler.add(new UserCommandHandler(c, writer, selector, refresher));
        commandHandler.add(new DeviceCommandHandler(c, writer, selector, refresher));

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
