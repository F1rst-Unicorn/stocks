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
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.reactivex.rxjava3.core.Observable.just;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodItemListInteractorImplTest {

    private FoodItemListInteractor uut;

    @Mock
    private FoodItemListRepository repository;

    @BeforeEach
    void setUp() {
        uut = new FoodItemListInteractorImpl(repository, new Localiser(Instant::now));
    }

    @Test
    void updatingItemsTwiceFiresUpdate() {
        AtomicInteger counter = new AtomicInteger();
        Id<Food> input = IdImpl.create(42);
        BehaviorSubject<List<FoodItemForListingData>> subject = BehaviorSubject.createDefault(emptyList());
        when(repository.get(input)).thenReturn(subject);
        when(repository.getFood(input)).thenReturn(just(FoodForSelection.create(1, "Banana")));
        var observable = uut.get(input);
        observable.subscribe(v -> counter.getAndIncrement());

        subject.onNext(emptyList());

        assertEquals(2, counter.get());
    }
}