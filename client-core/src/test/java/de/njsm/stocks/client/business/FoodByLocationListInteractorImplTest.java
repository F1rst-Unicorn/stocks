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

import de.njsm.stocks.client.business.entities.Entity;
import de.njsm.stocks.client.business.entities.FoodForListing;
import de.njsm.stocks.client.business.entities.Identifiable;
import de.njsm.stocks.client.business.entities.Location;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void gettingFoodIsForwarded() {
        Identifiable<Location> id = () -> 42;
        Observable<List<FoodForListing>> expected = Observable.empty();
        when(repository.getFoodBy(equalBy(id))).thenReturn(expected);

        @SuppressWarnings("ReactiveStreamsUnusedPublisher")
        Observable<List<FoodForListing>> actual = uut.getFoodBy(id);

        assertEquals(expected, actual);
        verify(repository).getFoodBy(equalBy(id));
    }

    private <T extends Entity<T>> Identifiable<T> equalBy(Identifiable<T> id) {
        return argThat(eqBy(id));
    }

    private <T extends Entity<T>> ArgumentMatcher<Identifiable<T>> eqBy(Identifiable<T> id) {
        return actual -> actual.id() == id.id();
    }
}