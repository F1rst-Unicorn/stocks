package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Food;
import de.njsm.stocks.linux.client.exceptions.SelectException;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.ArrayList;
import java.util.List;

public class FoodCommand extends Command {

    protected final CommandManager m;

    public FoodCommand(Configuration c) {
        command = "food";
        description = "Manage the food types";
        this.c = c;

        List<Command> commands = new ArrayList<>();
        commands.add(new FoodAddCommand(c));
        commands.add(new FoodListCommand(c));
        commands.add(new FoodRenameCommand(c));
        commands.add(new FoodRemoveCommand(c));
        m = new CommandManager(commands, command);
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.isEmpty()) {
            new FoodListCommand(c).listFood();
        } else {
            m.handleCommand(commands);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }

    public static int selectFood(Food[] f, String name) throws SelectException {
        InputReader scanner = new InputReader(System.in);
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
            System.out.print("Choose one (default " + f[0].id + "): ");
            result = scanner.nextInt(f[0].id);
        }
        return result;
    }

}
