package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Food;
import de.njsm.stocks.linux.client.data.Location;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

public class FoodRenameCommand extends Command {

    public FoodRenameCommand(Configuration c) {
        this.c = c;
        this.command = "rename";
        this.description = "Rename a food type";
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 2) {
            renameFood(commands.get(0), commands.get(1));
        } else {
            renameFood();
        }
    }

    public void renameFood() {
        InputReader scanner = new InputReader(System.in);
        System.out.print("Rename a food type\nName: ");
        String name = scanner.next();
        System.out.print("New name: ");
        String newName = scanner.next();
        renameFood(name, newName);
    }

    public void renameFood(String name, String newName) {
        Food[] l = c.getDatabaseManager().getFood(name);
        int id = FoodCommand.selectFood(l, name);

        for (Food food : l) {
            if (food.id == id){
                c.getServerManager().renameFood(food, newName);
                (new RefreshCommand(c)).refreshFood();
            }
        }

    }
}
