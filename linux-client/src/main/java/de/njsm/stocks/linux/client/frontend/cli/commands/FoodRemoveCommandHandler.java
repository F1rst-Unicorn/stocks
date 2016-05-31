package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Food;
import de.njsm.stocks.linux.client.exceptions.SelectException;

import java.util.List;

public class FoodRemoveCommandHandler extends CommandHandler {

    public FoodRemoveCommandHandler(Configuration c) {
        this.c = c;
        this.command = "remove";
        this.description = "Remove food from the system";
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1) {
            removeFood(commands.get(0));
        } else {
            removeFood();
        }
    }

    public void removeFood() {
        String name = c.getReader().next("Remove food\nName: ");
        removeFood(name);
    }

    public void removeFood(String name) {
        try {
            Food[] f = c.getDatabaseManager().getFood(name);
            int id = FoodCommandHandler.selectFood(f, name);

            for (Food food : f) {
                if (food.id == id) {
                    c.getServerManager().removeFood(food);
                    (new RefreshCommandHandler(c)).refreshFood();
                }
            }
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }
}
