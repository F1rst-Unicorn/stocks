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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class ErrorRetryInteractorImplTest {

    private ErrorRetryInteractorImpl uut;

    @Mock
    private LocationAddInteractor locationAddInteractor;

    @Mock
    private EntityDeleter<Location> locationDeleter;

    @Mock
    private LocationEditInteractor locationEditInteractor;

    @Mock
    private UnitAddInteractor unitAddInteractor;

    @Mock
    private EntityDeleter<Unit> unitDeleter;

    @Mock
    private UnitEditInteractor unitEditInteractor;

    @Mock
    private ScaledUnitAddInteractor scaledUnitAddInteractor;

    @Mock
    private ScaledUnitEditInteractor scaledUnitEditInteractor;

    @Mock
    private EntityDeleter<ScaledUnit> scaledUnitDeleter;

    @Mock
    private FoodAddInteractor foodAddInteractor;

    @Mock
    private EntityDeleter<Food> foodDeleter;

    @Mock
    private FoodEditInteractor foodEditInteractor;

    @Mock
    private FoodItemAddInteractor foodItemAddInteractor;

    @Mock
    private EntityDeleter<FoodItem> foodItemDeleter;

    @Mock
    private FoodItemEditInteractor foodItemEditInteractor;

    @Mock
    private EanNumberListInteractor eanNumberListInteractor;

    @Mock
    private Synchroniser synchroniser;

    @Mock
    private ErrorRepository errorRepository;

    @Mock
    private Scheduler scheduler;

    @BeforeEach
    void setUp() {
        uut = new ErrorRetryInteractorImpl(locationAddInteractor,
                locationDeleter,
                locationEditInteractor,
                unitAddInteractor,
                unitDeleter,
                unitEditInteractor,
                scaledUnitAddInteractor,
                scaledUnitEditInteractor,
                scaledUnitDeleter,
                foodAddInteractor,
                foodDeleter,
                foodEditInteractor,
                foodItemAddInteractor,
                foodItemDeleter,
                foodItemEditInteractor,
                eanNumberListInteractor,
                synchroniser,
                scheduler,
                errorRepository);
    }

    @Test
    void retryingQueuesJob() {
        LocationAddForm locationAddForm = LocationAddForm.create("Fridge", "the cold one");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", locationAddForm);

        uut.retry(input);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.ADD_LOCATION, captor.getValue().name());
    }

    @Test
    void deletingErrorQueuesTask() {
        LocationAddForm locationAddForm = LocationAddForm.create("Fridge", "the cold one");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", locationAddForm);

        uut.delete(input);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.DELETE_ERROR, captor.getValue().name());
    }

    @Test
    void deletingAnErrorInBackgroundForwardsToRepository() {
        LocationAddForm locationAddForm = LocationAddForm.create("Fridge", "the cold one");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", locationAddForm);

        uut.deleteInBackground(input);

        verify(errorRepository).deleteError(input);
        verifyNoInteractions(locationAddInteractor);
        verifyNoInteractions(synchroniser);
    }

    @Test
    void retryingToAddLocationDispatchesToLocationAdder() {
        LocationAddForm locationAddForm = LocationAddForm.create("Fridge", "the cold one");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", locationAddForm);

        uut.retryInBackground(input);

        verify(locationAddInteractor).addLocation(locationAddForm);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingSynchronisationDispatches() {
        SynchronisationErrorDetails synchronisationErrorDetails = SynchronisationErrorDetails.create();
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", synchronisationErrorDetails);

        uut.retryInBackground(input);

        verify(synchroniser).synchronise();
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingLocationDeletingDispatches() {
        LocationDeleteErrorDetails locationDeleteErrorDetails = LocationDeleteErrorDetails.create(1, "Fridge");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", locationDeleteErrorDetails);

        uut.retryInBackground(input);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Id<Location>> captor = ArgumentCaptor.forClass(Id.class);
        verify(locationDeleter).delete(captor.capture());
        assertEquals(locationDeleteErrorDetails.id(), captor.getValue().id());
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingLocationEditingDispatches() {
        LocationEditErrorDetails locationEditErrorDetails = LocationEditErrorDetails.create(1, "Fridge", "The cold one");
        LocationToEdit expected = LocationToEdit.builder()
                .id(locationEditErrorDetails.id())
                .name(locationEditErrorDetails.name())
                .description(locationEditErrorDetails.description())
                .build();
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", locationEditErrorDetails);

        uut.retryInBackground(input);

        verify(locationEditInteractor).edit(expected);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingUnitAddingDispatches() {
        UnitAddForm errorDetails = UnitAddForm.create("name", "abbreviation");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", errorDetails);

        uut.retryInBackground(input);

        verify(unitAddInteractor).addUnit(errorDetails);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingUnitDeletingDispatches() {
        UnitDeleteErrorDetails unitDeleteErrorDetails = UnitDeleteErrorDetails.create(1, "Gramm", "g");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", unitDeleteErrorDetails);

        uut.retryInBackground(input);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Id<Unit>> captor = ArgumentCaptor.forClass(Id.class);
        verify(unitDeleter).delete(captor.capture());
        assertEquals(unitDeleteErrorDetails.id(), captor.getValue().id());
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingUnitEditingDispatches() {
        UnitEditErrorDetails unitEditErrorDetails = UnitEditErrorDetails.create(1, "Gramm", "g");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", unitEditErrorDetails);

        uut.retryInBackground(input);

        ArgumentCaptor<UnitToEdit> captor = ArgumentCaptor.forClass(UnitToEdit.class);
        verify(unitEditInteractor).edit(captor.capture());
        assertEquals(unitEditErrorDetails.id(), captor.getValue().id());
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingScaledUnitAddingDispatches() {
        ScaledUnitAddErrorDetails errorDetails = ScaledUnitAddErrorDetails.create(BigDecimal.ONE, 2, "Gramm", "g");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", errorDetails);

        uut.retryInBackground(input);

        ScaledUnitAddForm expected = ScaledUnitAddForm.create(errorDetails.scale(), errorDetails.unit());
        verify(scaledUnitAddInteractor).add(expected);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingScaledUnitEditingDispatches() {
        ScaledUnitEditErrorDetails errorDetails = ScaledUnitEditErrorDetails.create(1, BigDecimal.ONE, 2, "Gramm", "g");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", errorDetails);
        ScaledUnitToEdit expected = ScaledUnitToEdit.create(errorDetails.id(), errorDetails.scale(), errorDetails.unit());

        uut.retryInBackground(input);

        verify(scaledUnitEditInteractor).edit(expected);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingScaledUnitDeletingDispatches() {
        ScaledUnitDeleteErrorDetails scaledUnitDeleteErrorDetails = ScaledUnitDeleteErrorDetails.create(1, BigDecimal.ONE, "Gramm", "g");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", scaledUnitDeleteErrorDetails);

        uut.retryInBackground(input);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Id<ScaledUnit>> captor = ArgumentCaptor.forClass(Id.class);
        verify(scaledUnitDeleter).delete(captor.capture());
        assertEquals(scaledUnitDeleteErrorDetails.id(), captor.getValue().id());
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingFoodAddingDispatches() {
        FoodAddErrorDetails errorDetails = FoodAddErrorDetails.create(
                "Banana",
                true,
                Period.ZERO,
                1,
                2,
                "they are yellow",
                "Cupboard",
                FoodAddErrorDetails.StoreUnit.create(BigDecimal.TEN, "g"));
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", errorDetails);

        uut.retryInBackground(input);

        FoodAddForm expected = FoodAddForm.create(errorDetails.name(),
                errorDetails.toBuy(),
                errorDetails.expirationOffset(),
                errorDetails.location().orElse(null),
                errorDetails.storeUnit(),
                errorDetails.description());
        verify(foodAddInteractor).add(expected);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingFoodDeletingDispatches() {
        FoodDeleteErrorDetails foodDeleteErrorDetails = FoodDeleteErrorDetails.create(1, "Banana");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", foodDeleteErrorDetails);

        uut.retryInBackground(input);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Id<Food>> captor = ArgumentCaptor.forClass(Id.class);
        verify(foodDeleter).delete(captor.capture());
        assertEquals(foodDeleteErrorDetails.id(), captor.getValue().id());
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingFoodEditingDispatches() {
        FoodEditErrorDetails errorDetails = FoodEditErrorDetails.create(1, "Banana", Period.ofDays(3), 4, 5, "yellow");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", errorDetails);
        FoodToEdit expected = FoodToEdit.create(errorDetails.id(), errorDetails.name(), errorDetails.expirationOffset(), errorDetails.location(), errorDetails.storeUnit(), errorDetails.description());

        uut.retryInBackground(input);

        verify(foodEditInteractor).edit(expected);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingFoodItemAddingDispatches() {
        FoodItemAddErrorDetails errorDetails = FoodItemAddErrorDetails.create(
                LocalDate.ofEpochDay(3),
                1,
                2,
                3,
                FoodItemAddErrorDetails.Unit.create(BigDecimal.TEN, "g"),
                "Banana",
                "Fridge");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", errorDetails);

        uut.retryInBackground(input);

        FoodItemForm expected = FoodItemForm.create(
                errorDetails.eatBy(),
                errorDetails.ofType(),
                errorDetails.storedIn(),
                errorDetails.unitId()
        );
        verify(foodItemAddInteractor).add(expected);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingFoodItemDeletingDispatches() {
        FoodItemDeleteErrorDetails foodDeleteErrorDetails = FoodItemDeleteErrorDetails.create(1, "Banana", FoodItemDeleteErrorDetails.Unit.create(BigDecimal.ONE, "g"));
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", foodDeleteErrorDetails);

        uut.retryInBackground(input);

        verify(foodItemDeleter).delete(Matchers.equalBy(foodDeleteErrorDetails));
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingFoodItemEditingDispatches() {
        FoodItemEditErrorDetails foodItemEditErrorDetails = FoodItemEditErrorDetails.create(1, "Banana", LocalDate.ofEpochDay(2), 3, 4);
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", foodItemEditErrorDetails);
        FoodItemToEdit expected = FoodItemToEdit.create(foodItemEditErrorDetails.id(), foodItemEditErrorDetails.eatBy(), foodItemEditErrorDetails.storedIn(), foodItemEditErrorDetails.unit());

        uut.retryInBackground(input);

        verify(foodItemEditInteractor).edit(expected);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingEanNumberAddingDispatches() {
        EanNumberAddErrorDetails eanNumberAddForm = EanNumberAddErrorDetails.create(2, "Banana", "123");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", eanNumberAddForm);
        EanNumberAddForm expected = EanNumberAddForm.create(eanNumberAddForm.identifies(), eanNumberAddForm.eanNumber());

        uut.retryInBackground(input);

        verify(eanNumberListInteractor).add(expected);
        verify(errorRepository).deleteError(input);
    }
}
