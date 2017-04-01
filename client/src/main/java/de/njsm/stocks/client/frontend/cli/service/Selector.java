package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.frontend.cli.InputReader;
import de.njsm.stocks.client.frontend.cli.ScreenWriter;
import de.njsm.stocks.common.data.FoodItem;

import java.util.List;

public class Selector {

    private ScreenWriter writer;

    private InputReader reader;

    public Selector(ScreenWriter writer, InputReader reader) {
        this.writer = writer;
        this.reader = reader;
    }

    public FoodItem selectItem(List<FoodItem> items) throws InputException {
        if (items.isEmpty()) {
            throw new InputException("No items found");
        } else if (items.size() == 1) {
            return items.get(0);
        } else {
            writer.printItems("Several items found: ", items);
            int resultId = reader.nextInt("Choose one ", items.get(0).id);
            FoodItem result = items.stream().filter(i -> i.id == resultId).findFirst().orElse(null);
            if (result == null) {
                throw new InputException("You did an invalid selection");
            } else {
                return result;
            }
        }
    }


}
