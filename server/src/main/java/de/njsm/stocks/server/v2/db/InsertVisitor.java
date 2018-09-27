package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.business.data.visitor.BaseVisitor;
import org.jooq.InsertSetStep;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.types.UInteger;

import java.sql.Timestamp;

import static de.njsm.stocks.server.v2.db.jooq.Tables.*;

public class InsertVisitor<T extends Record> extends BaseVisitor<InsertSetStep<T>, Query> {

    @Override
    public Query food(Food f, InsertSetStep<T> arg) {
        return arg.columns(FOOD.NAME, FOOD.VERSION)
                .values(f.name, UInteger.valueOf(f.version));
    }

    @Override
    public Query location(Location l, InsertSetStep<T> arg) {
        return arg.columns(LOCATION.NAME, LOCATION.VERSION)
                .values(l.name, UInteger.valueOf(l.version));
    }

    @Override
    public Query eanNumber(EanNumber n, InsertSetStep<T> arg) {
        return arg.columns(EAN_NUMBER.NUMBER, EAN_NUMBER.IDENTIFIES)
                .values(n.eanCode, UInteger.valueOf(n.identifiesFood));
    }

    @Override
    public Query foodItem(FoodItem i, InsertSetStep<T> input) {
        return input.columns(FOOD_ITEM.EAT_BY,
                FOOD_ITEM.STORED_IN,
                FOOD_ITEM.OF_TYPE,
                FOOD_ITEM.REGISTERS,
                FOOD_ITEM.BUYS)
                .values(new Timestamp(i.eatByDate.toEpochMilli()),
                        UInteger.valueOf(i.storedIn),
                        UInteger.valueOf(i.ofType),
                        UInteger.valueOf(i.registers),
                        UInteger.valueOf(i.buys));
    }
}
