package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.client.exceptions.SelectException;

public class FoodRenameCommandHandler extends CommandHandler {

    public FoodRenameCommandHandler(Configuration c) {
        this.c = c;
        this.command = "rename";
        this.description = "Rename a food type";
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            String foodName = command.next();
            if (command.hasNext()) {
                String newName = command.next();
                renameFood(foodName, newName);
            } else {
                renameFood();
            }
        } else {
            renameFood();
        }
    }

    public void renameFood() {
        String name = c.getReader().next("Rename a food type\nName: ");
        String newName = c.getReader().next("New name: ");
        renameFood(name, newName);
    }

    public void renameFood(String name, String newName) {
        try {
            Food[] l = c.getDatabaseManager().getFood(name);
            int id = FoodCommandHandler.selectFood(l, name);

            for (Food food : l) {
                if (food.id == id) {
                    c.getServerManager().renameFood(food, newName);
                    (new RefreshCommandHandler(c, false)).refresh();
                }
            }
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }

    }
}
