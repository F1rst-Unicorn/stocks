package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.frontend.cli.*;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.client.exceptions.InputException;

import java.util.ArrayList;
import java.util.List;

public class FoodCommandHandler extends AbstractCommandHandler {

    protected final CommandManager m;

    public FoodCommandHandler(Configuration c,
                              ScreenWriter writer) {
        super(writer);
        command = "food";
        description = "Manage the food types";
        this.c = c;

        List<AbstractCommandHandler> commands = new ArrayList<>();
        commands.add(new FoodAddCommandHandler(c, writer));
        commands.add(new FoodListCommandHandler(writer, c));
        commands.add(new FoodRenameCommandHandler(c, writer));
        commands.add(new FoodRemoveCommandHandler(c, writer));
        m = new CommandManager(commands, command);
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            m.handleCommand(command);
        } else {
            new FoodListCommandHandler(writer, c).handle(command);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }

    public static int selectFood(List<Food> f, String name) throws InputException {
        InputReader scanner = new InputReader(System.in);
        int result;

        if (f.size() == 1) {
            result = f.get(0).id;
        } else if (f.size() == 0) {
            throw new InputException("No such food found: " + name);
        } else {
            System.out.println("Several food types found");
            for (Food food : f) {
                System.out.println("\t" + food.id + ": " + food.name);
            }
            result = scanner.nextInt("Choose one ", f.get(0).id);
        }
        return result;
    }

}
