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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.business.Matchers.equalBy;
import static java.math.BigDecimal.valueOf;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodByLocationListInteractorImplTest {

    private FoodByLocationListInteractorImpl uut;

    @Mock
    private FoodListRepository repository;

    private Localiser localiser;

    @BeforeEach
    void setUp() {
        localiser = new Localiser(Instant::now);
        uut = new FoodByLocationListInteractorImpl(repository, new FoodRegrouper(localiser));
    }

    @Test
    void singleFoodWithSingleAmountIsReturned() {
        FoodForListingBaseData food = FoodForListingBaseData.create(1, "Banana", false, Instant.EPOCH);
        StoredFoodAmount amount = StoredFoodAmount.create(food.id(), 2, 3, valueOf(1), "piece", 1);
        FoodForListing expected = FoodForListing.create(food, singletonList(UnitAmount.of(valueOf(1), amount.abbreviation())), localiser);
        Id<Location> id = () -> 42;
        when(repository.getFoodBy(equalBy(id))).thenReturn(Observable.just(singletonList(food)));
        when(repository.getFoodAmountsIn(equalBy(id))).thenReturn(Observable.just(singletonList(amount)));

        Observable<List<FoodForListing>> actual = uut.getFoodBy(id);

        actual.test().assertValue(singletonList(expected));
        verify(repository).getFoodBy(equalBy(id));
        verify(repository).getFoodAmountsIn(equalBy(id));
    }
}