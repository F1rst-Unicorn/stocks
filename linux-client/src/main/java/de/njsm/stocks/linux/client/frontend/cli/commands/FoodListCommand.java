package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.FoodItem;
import de.njsm.stocks.linux.client.data.view.FoodView;
import de.njsm.stocks.linux.client.data.view.UserDeviceView;

import java.util.Iterator;
import java.util.List;

public class FoodListCommand extends Command {

    public FoodListCommand(Configuration c) {
        this.c = c;
        this.command = "list";
        this.description = "List food in stock";
    }

    @Override
    public void handle(List<String> commands) {
        listFood();
    }

    public void listFood() {

        FoodView[] food = c.getDatabaseManager().getItems();

        if (food.length == 0) {
            System.out.println("No food here...");
            return;
        }

        System.out.println("Current food: ");
        for (FoodView f : food) {
            System.out.println("\t" + f.getItems().size() + "x " + f.getFood().name);
            for (FoodItem i : f.getItems()) {
                System.out.println("\t\t" + i.eatByDate);
            }
        }

    }

}
