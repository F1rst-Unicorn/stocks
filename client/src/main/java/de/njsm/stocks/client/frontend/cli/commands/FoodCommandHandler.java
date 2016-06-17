package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.Configuration;
import de.njsm.stocks.client.data.Food;
import de.njsm.stocks.client.exceptions.SelectException;
import de.njsm.stocks.client.frontend.cli.EnhancedInputReader;
import de.njsm.stocks.client.frontend.cli.InputReader;

import java.util.ArrayList;
import java.util.List;

public class FoodCommandHandler extends CommandHandler {

    protected final CommandManager m;

    public FoodCommandHandler(Configuration c) {
        command = "food";
        description = "Manage the food types";
        this.c = c;

        List<CommandHandler> commands = new ArrayList<>();
        commands.add(new FoodAddCommandHandler(c));
        commands.add(new FoodListCommandHandler(c));
        commands.add(new FoodRenameCommandHandler(c));
        commands.add(new FoodRemoveCommandHandler(c));
        m = new CommandManager(commands, command);
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            m.handleCommand(command);
        } else {
            new FoodListCommandHandler(c).handle(command);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }

    public static int selectFood(Food[] f, String name) throws SelectException {
        InputReader scanner = new EnhancedInputReader(System.in);
        int result;

        if (f.length == 1) {
            result = f[0].id;
        } else if (f.length == 0) {
            throw new SelectException("No such food found: " + name);
        } else {
            System.out.println("Several food types found");
            for (Food food : f) {
                System.out.println("\t" + food.id + ": " + food.name);
            }
            result = scanner.nextInt("Choose one ", f[0].id);
        }
        return result;
    }

}
