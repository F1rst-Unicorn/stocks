/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.db.jooq.tables.records.PriceRecord;
import org.jooq.Field;
import org.jooq.RecordMapper;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static de.njsm.stocks.server.v2.db.jooq.tables.GroceryStore.GROCERY_STORE;
import static de.njsm.stocks.server.v2.db.jooq.tables.Price.PRICE;
import static org.jooq.impl.DSL.select;

public class PriceHandler extends CrudDatabaseHandler<PriceRecord, Price> {

    private final GroceryChainHandler groceryChainHandler;

    public PriceHandler(ConnectionFactory connectionFactory, GroceryChainHandler groceryChainHandler) {
        super(connectionFactory);
        this.groceryChainHandler = groceryChainHandler;
    }

    @Override
    protected Table<PriceRecord> getTable() {
        return PRICE;
    }

    @Override
    protected RecordMapper<PriceRecord, Price> getDtoMap() {
        return cursor -> BitemporalPrice.builder()
                .id(cursor.getId())
                .version(cursor.getVersion())
                .validTimeStart(cursor.getValidTimeStart().toInstant())
                .validTimeEnd(cursor.getValidTimeEnd().toInstant())
                .transactionTimeStart(cursor.getTransactionTimeStart().toInstant())
                .transactionTimeEnd(cursor.getTransactionTimeEnd().toInstant())
                .initiates(cursor.getInitiates())
                .price(cursor.getPrice())
                .purchaseDate(cursor.getPurchaseDate().toInstant())
                .scale(cursor.getScale())
                .groceryStore(cursor.getGroceryStore())
                .food(cursor.getFood())
                .scaledUnit(cursor.getScaledUnit())
                .build();
    }

    @Override
    protected TableField<PriceRecord, Integer> getIdField() {
        return PRICE.ID;
    }

    @Override
    protected TableField<PriceRecord, Integer> getVersionField() {
        return PRICE.VERSION;
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                PRICE.ID,
                PRICE.VERSION,
                PRICE.PRICE_,
                PRICE.PURCHASE_DATE,
                PRICE.SCALE,
                PRICE.GROCERY_STORE,
                PRICE.FOOD,
                PRICE.SCALED_UNIT
        );
    }

    public StatusCode deletePricesOfChain(Versionable<GroceryChain> id) {
        return runCommand(context -> {
            if (groceryChainHandler.isCurrentlyMissing(id, context)) {
                return StatusCode.NOT_FOUND;
            }

            Field<OffsetDateTime> now = DSL.currentOffsetDateTime();
            return currentDelete(PRICE.GROCERY_STORE.in(
                    select(GROCERY_STORE.ID)
                    .from(GROCERY_STORE)
                    .where(GROCERY_STORE.GROCERY_CHAIN.eq(id.id()))
                    .and(GROCERY_STORE.VALID_TIME_START.le(now))
                    .and(now.lt(GROCERY_STORE.VALID_TIME_END))
                    .and(GROCERY_STORE.TRANSACTION_TIME_END.eq(INFINITY))
            ))
                    .map(this::notFoundIsOk);
        });
    }
}
