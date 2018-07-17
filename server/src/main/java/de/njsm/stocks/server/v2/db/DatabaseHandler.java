package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;
import fj.data.Validation;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.types.UInteger;

import java.util.ArrayList;
import java.util.List;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD;

public class DatabaseHandler extends FailSafeDatabaseHandler {

    public DatabaseHandler(String url,
                           String username,
                           String password,
                           String resourceIdentifier) {
        super(url, username, password, resourceIdentifier);
    }

    public StatusCode addFood(String name) {
        return runCommand(context -> {
           context.insertInto(FOOD, FOOD.NAME, FOOD.VERSION)
                   .values(name, UInteger.valueOf(1))
                   .execute();
           return StatusCode.SUCCESS;
        });
    }

    public Validation<StatusCode, List<Food>> getFood() {
        return runQuery(context -> {
            Result<FoodRecord> cursor = context
                    .selectFrom(FOOD)
                    .fetch();

            List<Food> result = new ArrayList<>();
            for (FoodRecord r : cursor) {
                Food item = new Food();
                item.id = r.getId().intValue();
                item.name = r.getName();
                item.version = r.getVersion().intValue();
            }
            return Validation.success(result);
        });
    }

    public StatusCode renameFood(int id, int version, String newName) {
        return runCommand(context -> {
            if (isMissing(id, context))
                return StatusCode.NOT_FOUND;

            int changedItems = context.update(FOOD)
                    .set(FOOD.NAME, newName)
                    .where(FOOD.ID.eq(UInteger.valueOf(id))
                            .and(FOOD.VERSION.eq(UInteger.valueOf(version))))
                    .execute();

            if (changedItems == 1) {
                return StatusCode.SUCCESS;
            } else {
                return StatusCode.INVALID_DATA_VERSION;
            }
        });
    }

    public StatusCode deleteFood(int id, int version) {
        return runCommand(context -> {
            if (isMissing(id, context))
                return StatusCode.NOT_FOUND;

            int changedItems = context.deleteFrom(FOOD)
                    .where(FOOD.ID.eq(UInteger.valueOf(id))
                            .and(FOOD.VERSION.eq(UInteger.valueOf(version))))
                    .execute();

            if (changedItems == 1) {
                return StatusCode.SUCCESS;
            } else {
                return StatusCode.INVALID_DATA_VERSION;
            }
        });
    }

    private boolean isMissing(int id, DSLContext context) {
        int count = context.selectFrom(FOOD)
                .where(FOOD.ID.eq(UInteger.valueOf(id)))
                .fetch()
                .size();

        return count == 0;
    }
}
