package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Food;
import de.njsm.stocks.linux.client.data.FoodItem;
import de.njsm.stocks.linux.client.data.Location;
import de.njsm.stocks.linux.client.exceptions.SelectException;
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
        } else if (commands.size() == 1) {
            eatFood(commands.get(0));
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
        try {
            Food[] foods = c.getDatabaseManager().getFood(type);
            int foodId = FoodCommand.selectFood(foods, type);
            int itemId = c.getDatabaseManager().getNextItem(foodId);

            FoodItem item = new FoodItem();
            item.id = itemId;
            item.ofType = foodId;
            item.buys = c.getUserId();
            item.registers = c.getDeviceId();

            c.getServerManager().removeItem(item);
            (new RefreshCommand(c)).refreshFoodItems();
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }

}
