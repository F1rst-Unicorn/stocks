package de.njsm.stocks.client.frontend.cli.commands.food;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.Food;

import java.util.List;

public class InputCollector extends Selector {

    private DatabaseManager dbManager;

    public InputCollector(ScreenWriter writer, InputReader reader, DatabaseManager dbManager) {
        super(writer, reader);
        this.dbManager = dbManager;
    }

    public Food resolveFood(Command c) throws DatabaseException, InputException {
        String name = getName(c);
        List<Food> food = dbManager.getFood(name);
        return selectFood(food, name);    }

    public Food resolveNewFood(Command c) {
        Food result = new Food();
        result.name = getName(c);
        return result;
    }

    public String getName(Command c) {
        return getName("New food's name: ", c);
    }

    public String getName(String prompt, Command c) {
        if (c.hasNext()) {
            return c.next();
        } else {
            return reader.next(prompt);
        }
    }
}
