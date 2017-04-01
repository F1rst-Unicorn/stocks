package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.ScreenWriter;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.config.Configuration;

import java.util.List;

public class FoodRemoveCommandHandler extends AbstractCommandHandler {

    public FoodRemoveCommandHandler(Configuration c, ScreenWriter writer) {
        super(writer);
        this.c = c;
        this.command = "remove";
        this.description = "Remove food from the system";
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            removeFood(command.next());
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
            List<Food> f = c.getDatabaseManager().getFood(name);
            int id = FoodCommandHandler.selectFood(f, name);

            for (Food food : f) {
                if (food.id == id) {
                    c.getServerManager().removeFood(food);
                    (new RefreshCommandHandler(c, writer, false)).refresh();
                }
            }
        } catch (InputException |
                DatabaseException |
                NetworkException e) {
            System.out.println(e.getMessage());
        }
    }
}
