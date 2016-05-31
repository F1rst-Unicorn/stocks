package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Food;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

public class FoodAddCommand extends Command {

    public FoodAddCommand(Configuration c) {
        this.c = c;
        this.command = "add";
        this.description = "Add a new food type";
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1) {
            addFood(commands.get(0));
        } else {
            addFood();
        }
    }

    public void addFood() {
        System.out.print("Creating a new food type\nName: ");
        String name = c.getReader().nextName();
        addFood(name);
    }

    public void addFood(String name) {
        Food f = new Food();
        f.name = name;

        c.getServerManager().addFood(f);

        (new RefreshCommand(c)).refreshFood();
    }
}
