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

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

class FoodRegrouper {

    @Inject
    FoodRegrouper() {
    }

    List<FoodForListing> regroup(List<FoodForListingBaseData> food, List<StoredFoodAmount> amounts) {
        Map<Integer, List<FoodForListingBaseData>> foodById = food.stream().collect(groupingBy(FoodForListingBaseData::id));
        Map<Integer, List<StoredFoodAmount>> amountsByFoodId = amounts.stream().collect(groupingBy(StoredFoodAmount::foodId));

        return foodById.values().stream().map(v -> v.get(0))
                .map(v -> regroupSingleFood(v, amountsByFoodId.get(v.id())))
                .sorted(comparing(FoodForListing::nextEatByDate))
                .collect(toList());
    }

    private static FoodForListing regroupSingleFood(FoodForListingBaseData food, List<StoredFoodAmount> storedFoodAmounts) {
        List<UnitAmount> amountsSummedByUnit = storedFoodAmounts.stream().collect(groupingBy(StoredFoodAmount::unitId))
                .values().stream()
                .map(FoodRegrouper::addAmountsOfSameUnit)
                .collect(toList());
        return FoodForListing.create(food, amountsSummedByUnit);
    }

    private static UnitAmount addAmountsOfSameUnit(List<StoredFoodAmount> singleUnitAmounts) {
        BigDecimal amount = singleUnitAmounts.stream()
                .map(v -> v.scale().multiply(valueOf(v.numberOfFoodItemsWithSameScaledUnit())))
                .reduce(ZERO, BigDecimal::add);
        return UnitAmount.of(amount, singleUnitAmounts.get(0).abbreviation());
    }
}
