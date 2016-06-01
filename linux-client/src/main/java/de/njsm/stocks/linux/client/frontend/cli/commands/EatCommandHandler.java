package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Food;
import de.njsm.stocks.linux.client.data.FoodItem;
import de.njsm.stocks.linux.client.exceptions.SelectException;

import java.util.List;

public class EatCommandHandler extends CommandHandler {

    public EatCommandHandler(Configuration c) {
        command = "eat";
        description = "Eat a food item";
        this.c = c;

    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            String word = command.next();
            if (word.equals("help")) {
                printHelp();
            } else {
                eatFood(word);
            }
        } else {
            eatFood();
        }
    }

   public void eatFood() {
        String type = c.getReader().next("What to eat?  ");
        eatFood(type);
    }

    public void eatFood(String type) {
        try {
            Food[] foods = c.getDatabaseManager().getFood(type);
            int foodId = FoodCommandHandler.selectFood(foods, type);
            int itemId = c.getDatabaseManager().getNextItem(foodId);

            FoodItem item = new FoodItem();
            item.id = itemId;
            item.ofType = foodId;
            item.buys = c.getUserId();
            item.registers = c.getDeviceId();

            c.getServerManager().removeItem(item);
            (new RefreshCommandHandler(c)).refreshFoodItems();
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }

}
