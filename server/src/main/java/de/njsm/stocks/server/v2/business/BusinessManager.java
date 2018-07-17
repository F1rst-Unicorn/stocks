package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.DatabaseHandler;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class BusinessManager {

    private static final Logger LOG = LogManager.getLogger(BusinessManager.class);

    private DatabaseHandler databaseHandler;

    public BusinessManager(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public StatusCode addFood(String name) {
        return databaseHandler.addFood(name);
    }

    public Validation<StatusCode, List<Food>> getFood() {
        return databaseHandler.getFood();
    }

    public StatusCode renameFood(int id, int version, String newName) {
        Validation<StatusCode, Food> dbResult = databaseHandler.getFood(id);
        if (dbResult.isFail()) {
            return dbResult.fail();
        } else {
            return dbResult.success().ifVersionEquals(version,
                    () -> databaseHandler.renameFood(id, newName));
        }
    }

    public StatusCode deleteFood(int id, int version) {
        Validation<StatusCode, Food> dbResult = databaseHandler.getFood(id);
        if (dbResult.isFail()) {
            return dbResult.fail();
        } else {
            return dbResult.success().ifVersionEquals(version,
                    () -> databaseHandler.deleteFood(id));
        }
    }
}
