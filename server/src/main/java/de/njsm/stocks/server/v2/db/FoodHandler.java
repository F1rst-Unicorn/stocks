package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;
import org.jooq.Table;
import org.jooq.TableField;

import java.sql.Connection;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD;


public class FoodHandler extends CrudRenameDatabaseHandler<FoodRecord, Food> {


    public FoodHandler(Connection connection,
                       String resourceIdentifier,
                       InsertVisitor<FoodRecord> visitor) {
        super(connection, resourceIdentifier, visitor);
    }

    @Override
    protected Table<FoodRecord> getTable() {
        return FOOD;
    }

    @Override
    protected TableField<FoodRecord, Integer> getIdField() {
        return FOOD.ID;
    }

    @Override
    protected TableField<FoodRecord, Integer> getVersionField() {
        return FOOD.VERSION;
    }

    @Override
    protected TableField<FoodRecord, String> getNameColumn() {
        return FOOD.NAME;
    }

    @Override
    protected Function<FoodRecord, Food> getDtoMap() {
        return cursor -> new Food(
                cursor.getId(),
                cursor.getName(),
                cursor.getVersion()
        );
    }

}
