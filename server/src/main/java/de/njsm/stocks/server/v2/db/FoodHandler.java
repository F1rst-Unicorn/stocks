package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.types.UInteger;

import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD;


public class FoodHandler extends RenamableDatabaseHandler<FoodRecord, Food> {


    public FoodHandler(String url,
                       String username,
                       String password,
                       String resourceIdentifier,
                       InsertVisitor<FoodRecord> visitor) {
        super(url, username, password, resourceIdentifier, visitor);
    }

    @Override
    protected Table<FoodRecord> getTable() {
        return FOOD;
    }

    @Override
    protected TableField<FoodRecord, UInteger> getIdField() {
        return FOOD.ID;
    }

    @Override
    protected TableField<FoodRecord, UInteger> getVersionField() {
        return FOOD.VERSION;
    }

    @Override
    protected TableField<FoodRecord, String> getNameColumn() {
        return FOOD.NAME;
    }

    @Override
    protected Function<FoodRecord, Food> getDtoMap() {
        return cursor -> new Food(
                cursor.getId().intValue(),
                cursor.getName(),
                cursor.getVersion().intValue()
        );
    }

}
