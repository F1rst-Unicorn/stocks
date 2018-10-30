package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.data.*;
import de.njsm.stocks.server.v2.business.data.visitor.BaseVisitor;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static de.njsm.stocks.server.v2.db.jooq.Tables.*;

public class InsertVisitor<T extends Record> extends BaseVisitor<InsertSetStep<T>, InsertOnDuplicateStep<T>> {

    @Override
    public InsertOnDuplicateStep<T> food(Food f, InsertSetStep<T> arg) {
        return arg.columns(FOOD.NAME, FOOD.VERSION)
                .values(f.name, f.version);
    }

    @Override
    public InsertOnDuplicateStep<T> location(Location l, InsertSetStep<T> arg) {
        return arg.columns(LOCATION.NAME, LOCATION.VERSION)
                .values(l.name, l.version);
    }

    @Override
    public InsertOnDuplicateStep<T> eanNumber(EanNumber n, InsertSetStep<T> arg) {
        return arg.columns(EAN_NUMBER.NUMBER, EAN_NUMBER.IDENTIFIES)
                .values(n.eanCode, n.identifiesFood);
    }

    @Override
    public InsertOnDuplicateStep<T> foodItem(FoodItem i, InsertSetStep<T> input) {
        return input.columns(FOOD_ITEM.EAT_BY,
                FOOD_ITEM.STORED_IN,
                FOOD_ITEM.OF_TYPE,
                FOOD_ITEM.REGISTERS,
                FOOD_ITEM.BUYS)
                .values(OffsetDateTime.from(i.eatByDate.atOffset(ZoneOffset.UTC)),
                        i.storedIn,
                        i.ofType,
                        i.registers,
                        i.buys);
    }

    @Override
    public InsertOnDuplicateStep<T> userDevice(UserDevice userDevice, InsertSetStep<T> input) {
        return input.columns(USER_DEVICE.NAME, USER_DEVICE.BELONGS_TO)
                .values(userDevice.name, userDevice.userId);
    }

    @Override
    public InsertOnDuplicateStep<T> user(User u, InsertSetStep<T> arg) {
        return arg.columns(USER.NAME)
                .values(u.name);
    }
}
