package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Food;
import de.njsm.stocks.linux.client.data.FoodItem;
import de.njsm.stocks.linux.client.data.Location;
import de.njsm.stocks.linux.client.exceptions.SelectException;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddCommand extends Command {

    public AddCommand(Configuration c) {
        command = "add";
        description = "Add a food item";
        this.c = c;

    }

    @Override
    public void handle(List<String> commands) {
        if (! commands.isEmpty() &&
            commands.get(0).equals("help")) {
            printHelp();
        } else {
            addFood();
        }
    }


    public void addFood() {
        try {
            InputReader scanner = new InputReader(System.in);
            System.out.print("What to add?  ");
            String type = scanner.next();
            Food[] foods = c.getDatabaseManager().getFood(type);
            int foodId = FoodCommand.selectFood(foods, type);

            System.out.print("Where is it stored?  ");
            String location = scanner.next();
            Location[] locs = c.getDatabaseManager().getLocations(location);
            int locId = LocationCommand.selectLocation(locs, location);

            System.out.print("Eat before:  ");
            Date date = scanner.nextDate();

            FoodItem item = new FoodItem();
            item.ofType = foodId;
            item.storedIn = locId;
            item.buys = c.getUserId();
            item.registers = c.getDeviceId();
            item.eatByDate = date;

            c.getServerManager().addItem(item);
            (new RefreshCommand(c)).refreshFoodItems();
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }

}
