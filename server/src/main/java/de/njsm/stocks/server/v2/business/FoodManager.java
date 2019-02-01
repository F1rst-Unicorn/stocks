package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.FoodHandler;
import fj.data.Validation;

import java.util.List;

public class FoodManager extends BusinessObject {

    private FoodHandler dbHandler;

    public FoodManager(FoodHandler dbHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
    }

    public Validation<StatusCode, Integer> add(Food item) {
        return runFunction(() -> dbHandler.add(item));
    }

    public Validation<StatusCode, List<Food>> get() {
        return runFunction(() -> {
            dbHandler.setReadOnly();
            return dbHandler.get();
        });
    }

    public StatusCode rename(Food item, String newName) {
        return runOperation(() -> dbHandler.rename(item, newName));
    }

    public StatusCode delete(Food item) {
        return runOperation(() -> dbHandler.delete(item));
    }
}
