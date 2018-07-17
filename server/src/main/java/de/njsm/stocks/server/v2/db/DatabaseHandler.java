package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.BusinessManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Food;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.types.UInteger;

import java.util.ArrayList;
import java.util.List;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD;

public class DatabaseHandler extends FailSafeDatabaseHandler {

    private static final Logger LOG = LogManager.getLogger(BusinessManager.class);

    public DatabaseHandler(String url,
                           String username,
                           String password,
                           String resourceIdentifier) {
        super(url, username, password, resourceIdentifier);
    }

    public StatusCode addFood(String name) {
        return runOperation(context -> {
           context.insertInto(FOOD, FOOD.NAME, FOOD.VERSION)
                   .values(name, UInteger.valueOf(1))
                   .execute();
        });
    }

    public Validation<StatusCode, List<Food>> getFood() {
        return runOperation(context -> {
            Result<Record> cursor = context
                    .select()
                    .from(FOOD)
                    .fetch();

            List<Food> result = new ArrayList<>();
            for (Record r : cursor) {
                Food item = new Food();
                item.id = r.getValue(FOOD.ID).intValue();
                item.name = r.getValue(FOOD.NAME);
                item.version = r.getValue(FOOD.VERSION).intValue();
            }
            return Validation.success(result);
        });
    }

    public Validation<StatusCode, Food> getFood(int id) {
        return runOperation(context -> {
            Result<Record> cursor = context
                    .select()
                    .from(FOOD)
                    .where(FOOD.ID.eq(UInteger.valueOf(id)))
                    .fetch();

            if (cursor.isEmpty()) {
                LOG.info("Unknown food id " + id + "demanded");
                return Validation.fail(StatusCode.NOT_FOUND);
            } else {
                Food item = new Food();
                Record r = cursor.get(0);
                item.id = r.getValue(FOOD.ID).intValue();
                item.name = r.getValue(FOOD.NAME);
                item.version = r.getValue(FOOD.VERSION).intValue();
                return Validation.success(item);
            }
        });
    }

    public StatusCode renameFood(int id, String newName) {
        return runOperation(context -> {
            context.update(FOOD)
                    .set(FOOD.NAME, newName)
                    .where(FOOD.ID.eq(UInteger.valueOf(id)));
        });
    }

    public StatusCode deleteFood(int id) {
        return runOperation(context -> {
            context.deleteFrom(FOOD)
                    .where(FOOD.ID.eq(UInteger.valueOf(id)))
                    .execute();
        });
    }
}
