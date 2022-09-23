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
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodByLocationListInteractorImplTest {

    private FoodByLocationListInteractorImpl uut;

    @Mock
    private FoodListRepository repository;

    @BeforeEach
    void setUp() {
        uut = new FoodByLocationListInteractorImpl(repository);
    }

    @Test
    void singleFoodWithSingleAmountIsReturned() {
        FoodForListingBaseData food = FoodForListingBaseData.create(1, "Banana", false, Instant.EPOCH);
        StoredFoodAmount amount = StoredFoodAmount.create(food.id(), 2, 3, valueOf(1), "piece", 1);
        FoodForListing expected = FoodForListing.create(food, singletonList(UnitAmount.of(valueOf(1), amount.abbreviation())));
        testDataProcessing(
                singletonList(food),
                singletonList(amount),
                singletonList(expected));
    }

    @Test
    void scaleAndAmountIsMultiplied() {
        FoodForListingBaseData food = FoodForListingBaseData.create(1, "Banana", false, Instant.EPOCH);
        StoredFoodAmount amount = StoredFoodAmount.create(food.id(), 2, 3, valueOf(2), "piece", 3);
        FoodForListing expected = FoodForListing.create(food, singletonList(UnitAmount.of(valueOf(6), amount.abbreviation())));
        testDataProcessing(
                singletonList(food),
                singletonList(amount),
                singletonList(expected));
    }

    @Test
    void foodsAreOrderedByEatByDate() {
        FoodForListingBaseData food = FoodForListingBaseData.create(1, "Banana", false, Instant.EPOCH.plus(1, ChronoUnit.DAYS));
        StoredFoodAmount amount = StoredFoodAmount.create(food.id(), 2, 3, valueOf(2), "piece", 3);
        FoodForListing expected = FoodForListing.create(food, singletonList(UnitAmount.of(valueOf(6), amount.abbreviation())));
        FoodForListingBaseData food2 = FoodForListingBaseData.create(10, "Banana", false, Instant.EPOCH);
        StoredFoodAmount amount2 = StoredFoodAmount.create(food2.id(), 2, 3, valueOf(2), "piece", 3);
        FoodForListing expected2 = FoodForListing.create(food2, singletonList(UnitAmount.of(valueOf(6), amount2.abbreviation())));
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
        FoodForListing expected = FoodForListing.create(food, singletonList(UnitAmount.of(valueOf(3006), amount.abbreviation())));
        testDataProcessing(
                singletonList(food),
                asList(amount, amount2),
                singletonList(expected));
    }

    void testDataProcessing(List<FoodForListingBaseData> food, List<StoredFoodAmount> amounts, List<FoodForListing> expected) {
        Identifiable<Location> id = () -> 42;
        when(repository.getFoodBy(equalBy(id))).thenReturn(Observable.just(food));
        when(repository.getFoodAmountsIn(equalBy(id))).thenReturn(Observable.just(amounts));

        Observable<List<FoodForListing>> actual = uut.getFoodBy(id);

        actual.test().assertValue(expected);
        verify(repository).getFoodBy(equalBy(id));
        verify(repository).getFoodAmountsIn(equalBy(id));
    }

    private <T extends Entity<T>> Identifiable<T> equalBy(Identifiable<T> id) {
        return argThat(eqBy(id));
    }

    private <T extends Entity<T>> ArgumentMatcher<Identifiable<T>> eqBy(Identifiable<T> id) {
        return actual -> actual.id() == id.id();
    }
}