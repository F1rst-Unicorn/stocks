package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import fj.data.Validation;

import java.util.List;

public class FoodItemManager extends BusinessObject {

    private FoodItemHandler dbHandler;

    public FoodItemManager(FoodItemHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public Validation<StatusCode, Integer> add(FoodItem item) {
        return finishTransaction(dbHandler.add(item), dbHandler);
    }

    public Validation<StatusCode, List<FoodItem>> get() {
        return finishTransaction(dbHandler.get(), dbHandler);
    }

    public StatusCode edit(FoodItem item) {
        return finishTransaction(dbHandler.edit(item), dbHandler);
    }

    public StatusCode delete(FoodItem item) {
        return finishTransaction(dbHandler.delete(item), dbHandler);
    }
}
