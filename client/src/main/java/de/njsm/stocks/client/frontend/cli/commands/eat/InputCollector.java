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
        String name = resolveName(c);

        List<Food> foods = dbManager.getFood(name);
        Food food = selectFood(foods, name);
        return dbManager.getNextItem(food.id);
    }

    private String resolveName(Command c) {
        if (c.hasNext()) {
            return c.next();
        } else {
            return askForFoodName();
        }
    }

    private String askForFoodName() {
        return reader.next("What to eat?  ");
    }
}
