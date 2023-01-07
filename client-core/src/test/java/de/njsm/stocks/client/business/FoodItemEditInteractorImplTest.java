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
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static de.njsm.stocks.client.business.Matchers.equalBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodItemEditInteractorImplTest {

    private FoodItemEditInteractorImpl uut;

    @Mock
    private FoodItemEditRepository repository;

    @Mock
    private FoodItemEditService service;

    @Mock
    private Synchroniser synchroniser;

    @Mock
    private ErrorRecorder errorRecorder;

    @Mock
    private Scheduler scheduler;

    private Localiser localiser;

    @BeforeEach
    void setUp() {
        localiser = new Localiser(Instant::now);
        uut = new FoodItemEditInteractorImpl(repository, service, synchroniser, errorRecorder, scheduler, localiser);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(synchroniser);
    }

    @Test
    void gettingFormDataWorks() {
        FoodItemEditBaseData foodItem = FoodItemEditBaseData.create(1, FoodForSelection.create(2, "Banana"), Instant.EPOCH, 3, 4);
        List<LocationForSelection> locations = getLocations();
        List<ScaledUnitForSelection> units = getUnits();
        when(repository.getFoodItem(equalBy(foodItem))).thenReturn(Observable.just(foodItem));
        when(repository.getLocations()).thenReturn(Observable.just(locations));
        when(repository.getScaledUnits()).thenReturn(Observable.just(units));

        Observable<FoodItemEditFormData> actual = uut.getFormData(foodItem);

        actual.test().assertValue(FoodItemEditFormData.create(
                foodItem.id(),
                FoodForSelection.create(foodItem.food().id(), foodItem.food().name()),
                localiser.toLocalDate(foodItem.eatBy()),
                ListWithSuggestion.create(locations, 1),
                ListWithSuggestion.create(units, 1)
        ));
    }

    @Test
    void editingFromInterfaceQueuesTask() {
        uut.edit(getInput());

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.EDIT_FOOD_ITEM, captor.getValue().name());
    }

    @Test
    void editingWithoutChangeDoesNothing() {
        FoodItemToEdit input = getInput();
        FoodItemForEditing persistentVersion = input.withVersion(2, localiser);
        when(repository.getFoodItemForSending(input)).thenReturn(persistentVersion);

        uut.editInBackground(input);

        verifyNoInteractions(service);
        verify(repository).getFoodItemForSending(input);
    }

    @Test
    void differentDataSendsToService() {
        FoodItemToEdit localData = getInput();
        FoodItemToEdit editedFoodItem = FoodItemToEdit.create(1, LocalDate.EPOCH, 3, 4);
        FoodItemForEditing scaledUnitForEditing = localData.withVersion(2, localiser);
        FoodItemForEditing dataToNetwork = editedFoodItem.withVersion(2, localiser);
        when(repository.getFoodItemForSending(editedFoodItem)).thenReturn(scaledUnitForEditing);

        uut.editInBackground(editedFoodItem);

        verify(service).edit(dataToNetwork);
        verify(repository).getFoodItemForSending(editedFoodItem);
        verify(synchroniser).synchronise();
    }

    @Test
    void failingEditingIsRecorded() {
        FoodItemToEdit editedFoodItem = FoodItemToEdit.create(1, LocalDate.EPOCH, 3, 4);
        FoodItemForEditing foodItemForEditing = getInput().withVersion(2, localiser);
        FoodItemForEditing expected = editedFoodItem.withVersion(foodItemForEditing.version(), localiser);
        when(repository.getFoodItemForSending(editedFoodItem)).thenReturn(foodItemForEditing);
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        doThrow(exception).when(service).edit(expected);

        uut.editInBackground(editedFoodItem);

        verify(service).edit(expected);
        verify(repository).getFoodItemForSending(editedFoodItem);
        verify(errorRecorder).recordFoodItemEditError(exception, expected);
        verify(synchroniser).synchroniseAfterError(exception);
    }

    private FoodItemToEdit getInput() {
        return FoodItemToEdit.create(1, LocalDate.EPOCH, 2, 3);
    }

    private List<LocationForSelection> getLocations() {
        return Arrays.asList(
                LocationForSelection.create(2, "Basement"),
                LocationForSelection.create(3, "Fridge"),
                LocationForSelection.create(4, "Basement")
        );
    }

    private List<ScaledUnitForSelection> getUnits() {
        return Arrays.asList(
                ScaledUnitForSelection.create(2, "g", BigDecimal.ONE),
                ScaledUnitForSelection.create(4, "g", BigDecimal.ONE),
                ScaledUnitForSelection.create(6, "g", BigDecimal.ONE)
        );
    }
}