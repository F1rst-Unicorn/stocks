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

import de.njsm.stocks.client.business.entities.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

class FoodRegrouper {

    private final Localiser localiser;

    @Inject
    FoodRegrouper(Localiser localiser) {
        this.localiser = localiser;
    }

    @FunctionalInterface
    interface ResultFactory<I, O> {
        O build(I input, List<UnitAmount> amounts, Localiser localiser);
    }

    <O, I extends Id<Food>, U extends Comparable<? super U>>
    List<O> regroup(List<I> food, List<StoredFoodAmount> amounts, ResultFactory<I, O> factory, Function<O, U> sorter) {
        Map<Integer, List<I>> foodById = food.stream().collect(groupingBy(I::id));
        Map<Integer, List<StoredFoodAmount>> amountsByFoodId = amounts.stream().collect(groupingBy(StoredFoodAmount::foodId));

        return foodById.values().stream().map(v -> v.get(0))
                .map(v -> regroupSingleFood(v, amountsByFoodId.getOrDefault(v.id(), emptyList()), factory))
                .sorted(comparing(sorter))
                .collect(toList());
    }

    <O, I extends Id<Food>>
    O regroupSingleFood(I food, List<? extends UnitAmountForRegrouping> storedFoodAmounts, ResultFactory<I, O> factory) {
        return factory.build(food, regroupSingleFood(storedFoodAmounts), localiser);
    }

    List<UnitAmount> regroupSingleFood(List<? extends UnitAmountForRegrouping> storedFoodAmounts) {
        return storedFoodAmounts.stream().collect(groupingBy(UnitAmountForRegrouping::unitId))
                .values().stream()
                .map(FoodRegrouper::addAmountsOfSameUnit)
                .collect(toList());
    }

    private static UnitAmount addAmountsOfSameUnit(List<? extends UnitAmountForRegrouping> singleUnitAmounts) {
        BigDecimal amount = singleUnitAmounts.stream()
                .map(v -> v.scale().multiply(valueOf(v.numberOfFoodItemsWithSameScaledUnit())))
                .reduce(ZERO, BigDecimal::add);
        return UnitAmount.of(amount, singleUnitAmounts.get(0).abbreviation());
    }
}
