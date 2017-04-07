package de.njsm.stocks.client.frontend.cli.commands.eat;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.FoodItem;

import java.util.List;

public class InputCollector extends Selector {

    private final DatabaseManager dbManager;

    public InputCollector(ScreenWriter writer, InputReader reader, DatabaseManager dbManager) {
        super(writer, reader);
        this.dbManager = dbManager;
    }


    public FoodItem resolveItem(Command c) throws InputException, DatabaseException {
        String name;
        if (c.hasNext()) {
            name = c.next();
        } else {
            name = askForFoodName();
        }

        List<Food> foods = dbManager.getFood(name);
        int foodId = selectFood(foods, name).id;
        int itemId = dbManager.getNextItem(foodId);

        FoodItem item = new FoodItem();
        item.id = itemId;
        item.ofType = foodId;

        return item;
    }

    private String askForFoodName() {
        return reader.next("What to eat?  ");
    }
}
