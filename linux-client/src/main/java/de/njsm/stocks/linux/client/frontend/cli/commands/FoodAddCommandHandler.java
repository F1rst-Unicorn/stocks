package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Food;

import java.util.List;

public class FoodAddCommandHandler extends CommandHandler {

    public FoodAddCommandHandler(Configuration c) {
        this.c = c;
        this.command = "add";
        this.description = "Add a new food type";
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            addFood(command.next());
        } else {
            addFood();
        }
    }

    public void addFood() {
        String name = c.getReader().nextName("Creating a new food type\nName: ");
        addFood(name);
    }

    public void addFood(String name) {
        Food f = new Food();
        f.name = name;

        c.getServerManager().addFood(f);

        (new RefreshCommandHandler(c)).refreshFood();
    }
}
