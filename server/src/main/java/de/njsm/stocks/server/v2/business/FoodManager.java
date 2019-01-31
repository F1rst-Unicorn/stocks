package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.FoodHandler;
import fj.data.Validation;

import java.util.List;

public class FoodManager extends BusinessObject {

    private FoodHandler dbHandler;

    public FoodManager(FoodHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public Validation<StatusCode, Integer> add(Food item) {
        return finishTransaction(dbHandler.add(item), dbHandler);
    }

    public Validation<StatusCode, List<Food>> get() {
        return finishTransaction(dbHandler.get(), dbHandler);
    }

    public StatusCode rename(Food item, String newName) {
        return finishTransaction(dbHandler.rename(item, newName), dbHandler);
    }

    public StatusCode delete(Food item) {
        return finishTransaction(dbHandler.delete(item), dbHandler);
    }
}
