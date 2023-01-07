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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.FoodForListing;
import de.njsm.stocks.client.business.entities.FoodForListingBaseData;
import de.njsm.stocks.client.business.entities.StoredFoodAmount;
import de.njsm.stocks.client.business.entities.UnitAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FoodRegrouperTest {

    private FoodRegrouper uut;

    private Localiser localiser;

    @BeforeEach
    void setUp() {
        localiser = new Localiser(Instant::now);
        uut = new FoodRegrouper(localiser);
    }

    @Test
    void singleFoodWithSingleAmountIsReturned() {
        FoodForListingBaseData food = FoodForListingBaseData.create(1, "Banana", false, Instant.EPOCH);
        StoredFoodAmount amount = StoredFoodAmount.create(food.id(), 2, 3, valueOf(1), "piece", 1);
        FoodForListing expected = FoodForListing.create(food, singletonList(UnitAmount.of(valueOf(1), amount.abbreviation())), localiser);
        testDataProcessing(
                singletonList(food),
                singletonList(amount),
                singletonList(expected));
    }

    @Test
    void scaleAndAmountIsMultiplied() {
        FoodForListingBaseData food = FoodForListingBaseData.create(1, "Banana", false, Instant.EPOCH);
        StoredFoodAmount amount = StoredFoodAmount.create(food.id(), 2, 3, valueOf(2), "piece", 3);
        FoodForListing expected = FoodForListing.create(food, singletonList(UnitAmount.of(valueOf(6), amount.abbreviation())), localiser);
        testDataProcessing(
                singletonList(food),
                singletonList(amount),
                singletonList(expected));
    }

    @Test
    void foodsAreOrderedByEatByDate() {
        FoodForListingBaseData food = FoodForListingBaseData.create(1, "Banana", false, Instant.EPOCH.plus(1, ChronoUnit.DAYS));
        StoredFoodAmount amount = StoredFoodAmount.create(food.id(), 2, 3, valueOf(2), "piece", 3);
        FoodForListing expected = FoodForListing.create(food, singletonList(UnitAmount.of(valueOf(6), amount.abbreviation())), localiser);
        FoodForListingBaseData food2 = FoodForListingBaseData.create(10, "Banana", false, Instant.EPOCH);
        StoredFoodAmount amount2 = StoredFoodAmount.create(food2.id(), 2, 3, valueOf(2), "piece", 3);
        FoodForListing expected2 = FoodForListing.create(food2, singletonList(UnitAmount.of(valueOf(6), amount2.abbreviation())), localiser);
        testDataProcessing(
                asList(food, food2),
                asList(amount, amount2),
                asList(expected2, expected));
    }

    @Test
    void amountsOfSameUnitAreAdded() {
        FoodForListingBaseData food = FoodForListingBaseData.create(1, "Banana", false, Instant.EPOCH.plus(1, ChronoUnit.DAYS));
        StoredFoodAmount amount = StoredFoodAmount.create(food.id(), 2, 3, valueOf(2), "piece", 3);
        StoredFoodAmount amount2 = StoredFoodAmount.create(food.id(), 4, 3, valueOf(1000), "piece", 3);
        FoodForListing expected = FoodForListing.create(food, singletonList(UnitAmount.of(valueOf(3006), amount.abbreviation())), localiser);
        testDataProcessing(
                singletonList(food),
                asList(amount, amount2),
                singletonList(expected));
    }

    @Test
    void foodWithoutAmountYieldsEmptyAmount() {
        FoodForListingBaseData food = FoodForListingBaseData.create(1, "Banana", false, Instant.EPOCH.plus(1, ChronoUnit.DAYS));
        FoodForListing expected = FoodForListing.create(food, emptyList(), localiser);
        testDataProcessing(singletonList(food),
                emptyList(),
                singletonList(expected));
    }

    void testDataProcessing(List<FoodForListingBaseData> food, List<StoredFoodAmount> amounts, List<FoodForListing> expected) {
        List<FoodForListing> actual = uut.regroup(food, amounts, FoodForListing::create, FoodForListing::nextEatByDate);
        assertEquals(expected, actual);
    }
}