package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.ParseException;
import de.njsm.stocks.client.frontend.MainHandler;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.AggregatedCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.DefaultCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.add.AddCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.add.InputCollector;
import de.njsm.stocks.client.frontend.cli.commands.dev.DeviceAddCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.dev.DeviceListCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.dev.DeviceRemoveCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.eat.EatCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.food.FoodAddCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.food.FoodListCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.food.FoodRemoveCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.food.FoodRenameCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.loc.LocationAddCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.loc.LocationListCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.loc.LocationRemoveCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.loc.LocationRenameCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.move.MoveCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.refresh.RefreshCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.user.UserAddCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.user.UserListCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.user.UserRemoveCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.storage.DatabaseManager;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.ArrayList;


public class CliMainHandler implements MainHandler {

    private final AggregatedCommandHandler m;
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
        DatabaseManager dbManager = new DatabaseManager();
        Refresher refresher = new Refresher(c.getServerManager(), dbManager);

        InputCollector addCollector = new InputCollector(
                dbManager,
                reader,
                writer);
        de.njsm.stocks.client.frontend.cli.commands.move.InputCollector moveCollector =
                new de.njsm.stocks.client.frontend.cli.commands.move.InputCollector(
                        dbManager,
                        writer,
                        reader);

        de.njsm.stocks.client.frontend.cli.commands.eat.InputCollector eatCollector =
                new de.njsm.stocks.client.frontend.cli.commands.eat.InputCollector(writer, reader, dbManager);

        de.njsm.stocks.client.frontend.cli.commands.dev.InputCollector devCollector =
                new de.njsm.stocks.client.frontend.cli.commands.dev.InputCollector(reader, dbManager, writer);
        DeviceListCommandHandler listDevices = new DeviceListCommandHandler(writer, dbManager);
        DeviceAddCommandHandler addDevices = new DeviceAddCommandHandler(c, writer, refresher, devCollector, dbManager, c.getServerManager());
        DeviceRemoveCommandHandler removeDevices = new DeviceRemoveCommandHandler(writer, refresher, devCollector, c.getServerManager());
        DefaultCommandHandler devManager = new DefaultCommandHandler(writer, listDevices, "devices", "Manage the devices accessing the stocks system",
                listDevices, addDevices, removeDevices);

        de.njsm.stocks.client.frontend.cli.commands.user.InputCollector userCollector =
                new de.njsm.stocks.client.frontend.cli.commands.user.InputCollector(writer, reader, dbManager);
        UserListCommandHandler listUsers = new UserListCommandHandler(dbManager, writer);
        UserAddCommandHandler addUsers = new UserAddCommandHandler(writer, c.getServerManager(), userCollector, refresher);
        UserRemoveCommandHandler removeUsers = new UserRemoveCommandHandler(writer, c.getServerManager(), userCollector, refresher);
        DefaultCommandHandler userCommandHandler = new DefaultCommandHandler(writer, listUsers, "user", "Manage the users of the stocks system",
                listUsers, addUsers, removeUsers);

        de.njsm.stocks.client.frontend.cli.commands.loc.InputCollector locationCollector =
                new de.njsm.stocks.client.frontend.cli.commands.loc.InputCollector(writer, reader, dbManager);
        LocationListCommandHandler listLocs = new LocationListCommandHandler(writer, dbManager);
        LocationAddCommandHandler addLoc = new LocationAddCommandHandler(writer, refresher, locationCollector, c.getServerManager());
        LocationRemoveCommandHandler removeLoc = new LocationRemoveCommandHandler(writer, locationCollector, refresher, c.getServerManager());
        LocationRenameCommandHandler renameLoc = new LocationRenameCommandHandler(writer, locationCollector, refresher, c.getServerManager());
        DefaultCommandHandler locationCommandHandler = new DefaultCommandHandler(writer, listLocs, "loc", "Manage the locations to store food",
                listLocs, addLoc, removeLoc, renameLoc);

        de.njsm.stocks.client.frontend.cli.commands.food.InputCollector foodCollector =
                new de.njsm.stocks.client.frontend.cli.commands.food.InputCollector(writer, reader, dbManager);
        FoodListCommandHandler listFood = new FoodListCommandHandler(writer, dbManager);
        FoodAddCommandHandler addFood = new FoodAddCommandHandler(writer, refresher, foodCollector, c.getServerManager());
        FoodRemoveCommandHandler removeFood = new FoodRemoveCommandHandler(writer, foodCollector, refresher, c.getServerManager());
        FoodRenameCommandHandler renameFood = new FoodRenameCommandHandler(writer, foodCollector, refresher, c.getServerManager());
        DefaultCommandHandler foodCommandHandler = new DefaultCommandHandler(writer, listFood, "food", "Manage the food types",
                listFood, addFood, removeFood, renameFood);

        ArrayList<AbstractCommandHandler> commandHandler = new ArrayList<>();
        commandHandler.add(new AddCommandHandler(addCollector, c.getServerManager(), refresher, writer));
        commandHandler.add(new MoveCommandHandler(c.getServerManager(), moveCollector, writer, refresher));
        commandHandler.add(new EatCommandHandler(c, c.getServerManager(), writer, eatCollector, refresher));
        commandHandler.add(new RefreshCommandHandler(writer, refresher));
        commandHandler.add(foodCommandHandler);
        commandHandler.add(locationCommandHandler);
        commandHandler.add(userCommandHandler);
        commandHandler.add(devManager);

        m = new AggregatedCommandHandler(writer, commandHandler);
    }

    @Override
    public void run(String[] args) {
        boolean endRequested = false;
        Command command;

        if (args.length > 0) {
            try {
                command = Command.createCommand(args);
                m.handle(command);
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
                            m.handle(command);
                        } catch (ParseException e) {
                            // TODO Log
                        }
                }
            }
        }
        c.getReader().shutdown();
    }
}
