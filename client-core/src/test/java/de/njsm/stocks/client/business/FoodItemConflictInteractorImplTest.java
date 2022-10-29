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
import de.njsm.stocks.client.business.entities.conflict.FoodItemEditConflictData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.reactivex.rxjava3.core.Observable.just;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodItemConflictInteractorImplTest {

    private FoodItemConflictInteractorImpl uut;

    @Mock
    FoodItemEditRepository repository;

    @Mock
    ConflictRepository conflictRepository;

    @BeforeEach
    void setUp() {
        uut = new FoodItemConflictInteractorImpl(repository, conflictRepository);
    }

    @Test
    void gettingFormDataCallsBackends() {
        long input = 42;
        FoodItemEditConflictData data = FoodItemEditConflictData.create(1, 2, 3, "Banana",
                LocalDate.ofEpochDay(4), LocalDate.ofEpochDay(5), LocalDate.ofEpochDay(6),
                LocationForListing.create(7, "original location"),
                LocationForListing.create(8, "remote location"),
                LocationForListing.create(9, "locallocation"),
                ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                ScaledUnitForListing.create(12, "original", BigDecimal.valueOf(13)),
                ScaledUnitForListing.create(14, "original", BigDecimal.valueOf(15)));
        List<ScaledUnitForSelection> units = singletonList(ScaledUnitForSelection.create(1, "g", BigDecimal.ONE));
        when(repository.getScaledUnits()).thenReturn(just(units));
        List<LocationForSelection> locations = singletonList(LocationForSelection.create(2, "Fridge"));
        when(repository.getLocations()).thenReturn(just(locations));
        when(conflictRepository.getFoodItemEditConflict(input)).thenReturn(just(data));

        var actual = uut.getEditConflict(input);

        actual.test().awaitCount(1).assertValue(v ->
                v.id() == data.id() &&
                v.errorId() == data.errorId() &&
                v.originalVersion() == data.originalVersion() &&
                v.eatBy().equals(data.eatBy()) &&
                v.location().equals(data.storedIn()) &&
                v.unit().equals(data.unit()));
        verify(conflictRepository).getFoodItemEditConflict(input);
        verify(repository).getLocations();
        verify(repository).getScaledUnits();
    }
}