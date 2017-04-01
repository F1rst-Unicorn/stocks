package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.ScreenWriter;
import de.njsm.stocks.common.data.Food;

public class FoodAddCommandHandler extends AbstractCommandHandler {

    public FoodAddCommandHandler(Configuration c, ScreenWriter writer) {
        super(writer);
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
        try {
            Food f = new Food();
            f.name = name;

            c.getServerManager().addFood(f);

            (new RefreshCommandHandler(c, writer, false)).refresh();
        } catch (NetworkException e) {
            // TODO LOG
        }
    }
}
