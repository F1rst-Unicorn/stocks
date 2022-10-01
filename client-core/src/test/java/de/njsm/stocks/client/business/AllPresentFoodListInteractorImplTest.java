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
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Instant;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AllPresentFoodListInteractorImplTest {

    private AllPresentFoodListInteractor uut;

    @Mock
    private FoodListRepository repository;

    @BeforeEach
    void setUp() {
        uut = new AllPresentFoodListInteractorImpl(repository, new FoodRegrouper());
    }

    @Test
    void singleFoodWithSingleAmountIsReturned() {
        FoodForListingBaseData food = FoodForListingBaseData.create(1, "Banana", false, Instant.EPOCH);
        StoredFoodAmount amount = StoredFoodAmount.create(food.id(), 2, 3, valueOf(1), "piece", 1);
        FoodForListing expected = FoodForListing.create(food, singletonList(UnitAmount.of(valueOf(1), amount.abbreviation())));
        when(repository.getFood()).thenReturn(Observable.just(singletonList(food)));
        when(repository.getFoodAmounts()).thenReturn(Observable.just(singletonList(amount)));

        Observable<List<FoodForListing>> actual = uut.getFood();

        actual.test().assertValue(singletonList(expected));
        verify(repository).getFood();
        verify(repository).getFoodAmounts();
    }
}