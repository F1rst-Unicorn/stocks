package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import fj.data.Validation;

import java.util.List;

public class FoodItemManager extends BusinessObject {

    private FoodItemHandler dbHandler;

    public FoodItemManager(FoodItemHandler dbHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
    }

    public Validation<StatusCode, Integer> add(FoodItem item) {
        return runFunction(() -> dbHandler.add(item));
    }

    public Validation<StatusCode, List<FoodItem>> get() {
        return runFunction(() -> {
            dbHandler.setReadOnly();
            return dbHandler.get();
        });
    }

    public StatusCode edit(FoodItem item) {
        return runOperation(() -> dbHandler.edit(item));
    }

    public StatusCode delete(FoodItem item) {
        return runOperation(() -> dbHandler.delete(item));
    }
}
