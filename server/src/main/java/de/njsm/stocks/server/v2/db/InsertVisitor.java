package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.business.data.visitor.AbstractVisitor;
import org.jooq.InsertSetStep;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.types.UInteger;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD;

public class InsertVisitor<T extends Record> extends AbstractVisitor<InsertSetStep<T>, Query> {

    @Override
    public Query food(Food f, InsertSetStep<T> arg) {
        return arg.columns(FOOD.NAME, FOOD.VERSION)
                .values(f.name, UInteger.valueOf(f.version));
    }
}
