package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.common.data.Food;

import java.util.List;

public class FoodRenameCommandHandler extends AbstractCommandHandler {

    private Selector selector;

    private Refresher refresher;

    public FoodRenameCommandHandler(Configuration c, ScreenWriter writer, Selector selector, Refresher refresher) {
        super(writer);
        this.c = c;
        this.command = "rename";
        this.description = "Rename a food type";
        this.selector = selector;
        this.refresher = refresher;
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
            List<Food> l = c.getDatabaseManager().getFood(name);
            int id = selector.selectFood(l, name).id;

            for (Food food : l) {
                if (food.id == id) {
                    c.getServerManager().renameFood(food, newName);
                    refresher.refresh();
                }
            }
        } catch (InputException |
                DatabaseException |
                NetworkException e) {
            System.out.println(e.getMessage());
        }

    }
}
