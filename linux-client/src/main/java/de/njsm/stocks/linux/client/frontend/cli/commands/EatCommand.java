package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Food;
import de.njsm.stocks.linux.client.data.FoodItem;
import de.njsm.stocks.linux.client.data.Location;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.Date;
import java.util.List;

public class EatCommand extends Command {

    public EatCommand(Configuration c) {
        command = "eat";
        description = "Eat a food item";
        this.c = c;

    }

    @Override
    public void handle(List<String> commands) {
        if (! commands.isEmpty() &&
            commands.get(0).equals("help")) {
            printHelp();
        } else {
            eatFood();
        }
    }

    public void eatFood() {
        InputReader scanner = new InputReader(System.in);
        System.out.print("What to eat?  ");
        String type = scanner.next();
        eatFood(type);
    }

    public void eatFood(String type) {
        Food[] foods = c.getDatabaseManager().getFood(type);
        int foodId = FoodCommand.selectFood(foods, type);
        int itemId = c.getDatabaseManager().getNextItem(foodId);

        if (foodId == -1) {
            return;
        }
        if (itemId == -1) {
            System.out.println("You don't have any " + type + "...");
            return;
        }

        FoodItem item = new FoodItem();
        item.id = itemId;
        item.ofType = foodId;
        item.buys = c.getUserId();
        item.registers = c.getDeviceId();

        c.getServerManager().removeItem(item);
        (new RefreshCommand(c)).refreshFoodItems();
    }

}
