package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Food;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

public class FoodRemoveCommand extends Command {

    public FoodRemoveCommand(Configuration c) {
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
        InputReader scanner = new InputReader(System.in);
        System.out.print("Remove food\nName: ");
        String name = scanner.next();
        removeFood(name);
    }

    public void removeFood(String name) {
        Food[] f = c.getDatabaseManager().getFood(name);
        int id = FoodCommand.selectFood(f, name);

        for (Food food : f) {
            if (food.id == id){
                c.getServerManager().removeFood(food);
                (new RefreshCommand(c)).refreshFood();
            }
        }
    }
}
