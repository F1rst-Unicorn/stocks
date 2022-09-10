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

import de.njsm.stocks.client.business.entities.LocationForListing;
import de.njsm.stocks.client.business.entities.LocationForSelection;
import de.njsm.stocks.client.business.entities.ScaledUnitForListing;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import de.njsm.stocks.client.business.entities.conflict.FoodEditConflictData;
import de.njsm.stocks.client.business.entities.conflict.FoodEditConflictFormData;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Period;
import java.util.List;

import static io.reactivex.rxjava3.core.Observable.just;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodConflictInteractorImplTest {

    private FoodConflictInteractorImpl uut;

    @Mock
    FoodEditRepository repository;

    @Mock
    ConflictRepository conflictRepository;

    @BeforeEach
    void setUp() {
        uut = new FoodConflictInteractorImpl(repository, conflictRepository);
    }

    @Test
    void gettingFormDataCallsBackends() {
        long input = 42;
        FoodEditConflictData food = FoodEditConflictData.create(1, 2, 3,
                "original name", "remote name", "local name",
                Period.ofDays(4), Period.ofDays(5), Period.ofDays(6),
                of(LocationForListing.create(7, "original location")),
                of(LocationForListing.create(8, "remote location")),
                of(LocationForListing.create(9, "local location")),
                ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                ScaledUnitForListing.create(12, "remote", BigDecimal.valueOf(12)),
                ScaledUnitForListing.create(13, "local", BigDecimal.valueOf(14)),
                "original description", "remote description", "local description");
        List<ScaledUnitForSelection> units = singletonList(ScaledUnitForSelection.create(1, "g", BigDecimal.ONE));
        when(repository.getScaledUnitsForSelection()).thenReturn(just(units));
        List<LocationForSelection> locations = singletonList(LocationForSelection.create(2, "Fridge"));
        when(repository.getLocations()).thenReturn(just(locations));
        when(conflictRepository.getFoodEditConflict(input)).thenReturn(just(food));

        Observable<FoodEditConflictFormData> actual = uut.getEditConflict(input);

        actual.test().awaitCount(1).assertValue(v ->
                v.id() == food.id() &&
                v.errorId() == food.errorId() &&
                v.originalVersion() == food.originalVersion() &&
                v.name().equals(food.name()) &&
                v.expirationOffset().equals(food.expirationOffset()) &&
                v.location().equals(food.location()) &&
                v.storeUnit().equals(food.storeUnit()) &&
                v.description().equals(food.description()));
        verify(conflictRepository).getFoodEditConflict(input);
        verify(repository).getLocations();
        verify(repository).getScaledUnitsForSelection();
    }
}