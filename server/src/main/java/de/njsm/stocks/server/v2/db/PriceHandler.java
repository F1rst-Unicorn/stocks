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
import de.njsm.stocks.common.api.serialisers.InstantDeserialiser;
import de.njsm.stocks.server.v2.business.data.visitor.JooqInsertionVisitor;
import de.njsm.stocks.server.v2.db.jooq.tables.records.PriceRecord;
import fj.data.Validation;
import org.jooq.Field;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static de.njsm.stocks.common.api.StatusCode.NOT_FOUND;
import static de.njsm.stocks.server.v2.db.jooq.tables.Food.FOOD;
import static de.njsm.stocks.server.v2.db.jooq.tables.GroceryStore.GROCERY_STORE;
import static de.njsm.stocks.server.v2.db.jooq.tables.Price.PRICE;
import static de.njsm.stocks.server.v2.db.jooq.tables.ScaledUnit.SCALED_UNIT;
import static org.jooq.impl.DSL.select;

@Repository
@RequestScope
@Primary
public class PriceHandler extends CrudDatabaseHandler<PriceRecord, Price> {

    private final GroceryChainHandler groceryChainHandler;

    private final GroceryStoreHandler groceryStoreHandler;

    public PriceHandler(ConnectionFactory connectionFactory, GroceryChainHandler groceryChainHandler, GroceryStoreHandler groceryStoreHandler) {
        super(connectionFactory);
        this.groceryChainHandler = groceryChainHandler;
        this.groceryStoreHandler = groceryStoreHandler;
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
                .scale(cursor.getScale())
                .groceryStore(cursor.getGroceryStore())
                .food(cursor.getFood())
                .scaledUnit(cursor.getScaledUnit())
                .build();
    }

    @Override
    TableDescription<PriceRecord> tableDescription() {
        return new TableDescription.Price();
    }

    @Override
    public Validation<StatusCode, Integer> addReturningId(Insertable<Price> item) {
        return runFunction(context -> {

            if (!(item instanceof PriceForInsertion price)) {
                throw new IllegalArgumentException("unexpected typ for price insertion: " + item.getClass());
            }
            OffsetDateTime validTime;
            try {
                validTime = InstantDeserialiser.parseString(price.validTime()).atOffset(ZoneOffset.UTC);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            prolongValidTimeStart(context, new TableDescription.Food(), validTime, price.food(), FOOD.LOCATION, FOOD.STORE_UNIT)
                    .ifPresent(nextIds -> {

                Integer location = nextIds.get(FOOD.LOCATION);
                if (location != null) {
                    prolongValidTimeStart(context, new TableDescription.Location(), validTime, location);
                }

                var unit = prolongValidTimeStart(context, new TableDescription.ScaledUnit(), validTime, nextIds.get(FOOD.STORE_UNIT), SCALED_UNIT.UNIT);
                unit.ifPresent(v ->
                        prolongValidTimeStart(context, new TableDescription.Unit(), validTime, v.get(SCALED_UNIT.UNIT)));
            });

            prolongValidTimeStart(context, new TableDescription.GroceryStore(), validTime, price.groceryStore(), GROCERY_STORE.GROCERY_CHAIN)
                    .ifPresent(groceryChainId ->
                            prolongValidTimeStart(context, new TableDescription.GroceryChain(), validTime, groceryChainId.get(GROCERY_STORE.GROCERY_CHAIN)));

            prolongValidTimeStart(context, new TableDescription.ScaledUnit(), validTime, price.scaledUnit(), SCALED_UNIT.UNIT)
                    .ifPresent(unitId ->
                            prolongValidTimeStart(context, new TableDescription.Unit(), validTime, unitId.get(SCALED_UNIT.UNIT)));

            int lastInsertId = new JooqInsertionVisitor<PriceRecord>()
                    .visit(item, new JooqInsertionVisitor.Input<>(context.insertInto(tableDescription().table()), getPrincipals()))
                    .returning(tableDescription().id())
                    .fetch()
                    .getValue(0, tableDescription().id());
            return Validation.success(lastInsertId);
        });
    }

    /**
     * Prices must not be currentDeleted because they have been inserted to be
     * valid only for a single granule. Thus, terminate transaction time instead
     * to mark them as past knowledge without inserting a new entry valid until
     * now.
     */
    @Override
    public StatusCode delete(Versionable<Price> item) {
        return runCommand(context -> {
            Field<OffsetDateTime> now = DSL.currentOffsetDateTime();
            int changedItems = context.update(tableDescription().table())
                    .set(tableDescription().transactionTimeEnd(), now)
                    .where(tableDescription().id().eq(item.id()))
                    .execute();

            if (0 < changedItems)
                return StatusCode.SUCCESS;
            else
                return NOT_FOUND;
        });
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

    public StatusCode deletePricesOfStore(Versionable<GroceryStore> id) {
        return runCommand(context -> {
            if (groceryStoreHandler.isCurrentlyMissing(id, context)) {
                return StatusCode.NOT_FOUND;
            }

            Field<OffsetDateTime> now = DSL.currentOffsetDateTime();
            return currentDelete(PRICE.GROCERY_STORE.eq(id.id()))
                    .map(this::notFoundIsOk);
        });
    }
}
