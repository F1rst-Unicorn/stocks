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

import de.njsm.stocks.common.api.PriceForInsertion;
import de.njsm.stocks.common.api.serialisers.InstantSerialiser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;

import static de.njsm.stocks.server.v2.db.jooq.tables.Food.FOOD;
import static org.jooq.impl.DSL.min;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceHandlerTest extends DbTestCase {

    private PriceHandler uut;

    @BeforeEach
    void setUp() {
        GroceryChainHandler groceryChainHandler = new GroceryChainHandler(getConnectionFactory());
        uut = new PriceHandler(
                getConnectionFactory(),
                groceryChainHandler,
                new GroceryStoreHandler(getConnectionFactory(), groceryChainHandler));
    }

    @Test
    void insertingPriceDuringValidTimeOfReferredEntitiesDoesntChangeThem() {
        OffsetDateTime currentFoodTime = getDSLContext().select(min(FOOD.VALID_TIME_START))
                .from(FOOD)
                .where(FOOD.ID.eq(1))
                .fetchSingle(0, OffsetDateTime.class);

        PriceForInsertion input = PriceForInsertion.builder()
                .price(BigDecimal.ONE)
                .scale(BigDecimal.ONE)
                .validTime(InstantSerialiser.serialize(currentFoodTime.toInstant()))
                .food(1)
                .scaledUnit(1)
                .groceryStore(1)
                .build();

        var actual = uut.addReturningId(input);

        var commitResult = uut.commit();

        assertTrue(actual.isSuccess());
        assertTrue(commitResult.isSuccess());
    }

    @Test
    void insertingPriceBeforeValidTimeOfReferredEntitiesUpdatesThoseEntities() {
        String validTime = InstantSerialiser.serialize(Instant.EPOCH.minusSeconds(5));

        PriceForInsertion input = PriceForInsertion.builder()
                .price(BigDecimal.ONE)
                .scale(BigDecimal.ONE)
                .validTime(validTime)
                .food(1)
                .scaledUnit(2)
                .groceryStore(1)
                .build();

        var actual = uut.addReturningId(input);

        var commitResult = uut.commit();

        assertTrue(actual.isSuccess());
        assertTrue(commitResult.isSuccess());
    }
}