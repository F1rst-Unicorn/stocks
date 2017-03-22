package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.client.exceptions.SelectException;
import de.njsm.stocks.client.config.Configuration;

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
            List<Food> foods = c.getDatabaseManager().getFood(type);
            int foodId = FoodCommandHandler.selectFood(foods, type);
            int itemId = c.getDatabaseManager().getNextItem(foodId);

            FoodItem item = new FoodItem();
            item.id = itemId;
            item.ofType = foodId;
            item.buys = c.getUserId();
            item.registers = c.getDeviceId();

            c.getServerManager().removeItem(item);
            (new RefreshCommandHandler(c, false)).refresh();
        } catch (SelectException |
                DatabaseException |
                NetworkException e) {
            System.out.println(e.getMessage());
        }
    }

}
