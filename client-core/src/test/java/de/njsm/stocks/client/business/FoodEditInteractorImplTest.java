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
import de.njsm.stocks.client.execution.Scheduler;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Period;
import java.util.List;

import static io.reactivex.rxjava3.core.Observable.just;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FoodEditInteractorImplTest {

    private FoodEditInteractorImpl uut;

    @Mock
    private FoodEditRepository repository;

    @Mock
    private FoodEditService service;

    @Mock
    private Synchroniser synchroniser;

    @Mock
    private ErrorRecorder errorRecorder;

    @Mock
    private Scheduler scheduler;

    @BeforeEach
    void setUp() {
        uut = new FoodEditInteractorImpl(repository, service, synchroniser, errorRecorder, scheduler);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(synchroniser);
    }

    @Test
    void gettingFormDataWorks() {
        FoodToEdit food = getInput();
        when(repository.getFood(food)).thenReturn(just(food));
        List<ScaledUnitForSelection> units = singletonList(ScaledUnitForSelection.create(food.storeUnit(), "g", BigDecimal.ONE));
        when(repository.getScaledUnitsForSelection()).thenReturn(just(units));
        List<LocationForSelection> locations = singletonList(LocationForSelection.create(food.location().get(), "Fridge"));
        when(repository.getLocations()).thenReturn(just(locations));

        Observable<FoodEditingFormData> actual = uut.getFormData(food);

        actual.test().awaitCount(1).assertNoErrors().assertValue(v ->
                v.id() == food.id() &&
                        v.name().equals(food.name()) &&
                        v.expirationOffset().equals(food.expirationOffset()) &&
                        v.description().equals(food.description()) &&
                        v.storeUnits().equals(ListWithSuggestion.create(units, 0)) &&
                        v.locations().equals(locations) &&
                        v.currentLocationListPosition().get() == 0

        );
        verify(repository).getFood(food);
        verify(repository).getScaledUnitsForSelection();
        verify(repository).getLocations();
    }

    @Test
    void editingFromInterfaceQueuesTask() {
        uut.edit(getInput());

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.EDIT_FOOD, captor.getValue().name());
    }

    @Test
    void editingWithoutChangeDoesNothing() {
        FoodToEdit input = getInput();
        FoodForEditing persistentVersion = input.withVersion(2);
        when(repository.getFoodForSending(input)).thenReturn(persistentVersion);

        uut.editInBackground(input);

        verifyNoInteractions(service);
        verify(repository).getFoodForSending(input);
    }

    @Test
    void differentDataSendsToService() {
        FoodToEdit localData = getInput();
        FoodToEdit edited = FoodToEdit.create(
                localData.id(),
                localData.name() + " modified",
                localData.expirationOffset().plusDays(1),
                localData.location().get() + 1,
                localData.storeUnit() + 1,
                localData.description() + " modified"
                );
        FoodForEditing scaledUnitForEditing = getInput().withVersion(2);
        FoodForEditing dataToNetwork = edited.withVersion(2);
        when(repository.getFoodForSending(edited)).thenReturn(scaledUnitForEditing);

        uut.editInBackground(edited);

        verify(service).edit(dataToNetwork);
        verify(repository).getFoodForSending(edited);
        verify(synchroniser).synchronise();
    }

    @Test
    void failingEditingIsRecorded() {
        FoodToEdit editedFood = FoodToEdit.create(1, "Banana", Period.ofDays(2), 3, 5, "they are yellow");
        FoodForEditing foodForEditing = getInput().withVersion(2);
        FoodForEditing expected = editedFood.withVersion(foodForEditing.version());
        when(repository.getFoodForSending(editedFood)).thenReturn(foodForEditing);
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        doThrow(exception).when(service).edit(expected);

        uut.editInBackground(editedFood);

        verify(service).edit(expected);
        verify(repository).getFoodForSending(editedFood);
        verify(errorRecorder).recordFoodEditError(exception, expected);
        verify(synchroniser).synchroniseAfterError(exception);
    }

    private FoodToEdit getInput() {
        return FoodToEdit.create(1, "Banana", Period.ofDays(2), 3, 4, "they are yellow");
    }
}