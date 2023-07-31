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
import io.reactivex.rxjava3.core.Maybe;
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
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.njsm.stocks.client.business.Matchers.equalBy;
import static de.njsm.stocks.client.business.entities.IdImpl.create;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodItemAddInteractorImplTest {

    private FoodItemAddInteractorImpl uut;

    @Mock
    private FoodItemAddService service;

    @Mock
    private FoodItemAddRepository repository;

    @Mock
    private Scheduler scheduler;

    @Mock
    private Synchroniser synchroniser;

    @Mock
    private ErrorRecorder errorRecorder;

    private Localiser localiser;

    private Clock clock;

    @BeforeEach
    void setUp() {
        localiser = new Localiser(clock);
        clock = () -> Instant.EPOCH;
        uut = new FoodItemAddInteractorImpl(service, repository, scheduler, synchroniser, errorRecorder, localiser, clock,
                new FoodItemFieldPredictor(repository, localiser, clock));
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(synchroniser);
    }

    @Test
    void basicFormDataIsComputed() {
        FoodForItemCreation food = FoodForItemCreation.create(1, "Banana", Period.ofDays(10), Optional.of(create(3)), () -> 4);
        List<LocationForSelection> locations = getLocations();
        List<ScaledUnitForSelection> units = getUnits();
        when(repository.getFood(equalBy(food))).thenReturn(Maybe.just(food));
        when(repository.getLocations()).thenReturn(Maybe.just(locations));
        when(repository.getUnits()).thenReturn(Maybe.just(units));

        Maybe<FoodItemAddData> actual = uut.getFormData(food);

        actual.test().assertValue(FoodItemAddData.create(food.toSelection(),
                localiser.toLocalDate(clock.get().plus(food.expirationOffset())),
                ListWithSuggestion.create(locations, 1),
                ListWithSuggestion.create(units, 1)
        ));
    }

    @Test
    void missingFoodLocationTriggersPredictor() {
        FoodForItemCreation food = FoodForItemCreation.create(1, "Banana", Period.ofDays(10), Optional.empty(), () -> 4);
        List<LocationForSelection> locations = getLocations();
        List<ScaledUnitForSelection> units = getUnits();
        when(repository.getFood(equalBy(food))).thenReturn(Maybe.just(food));
        when(repository.getLocations()).thenReturn(Maybe.just(locations));
        when(repository.getUnits()).thenReturn(Maybe.just(units));
        when(repository.getLocationWithMostItemsOfType(food)).thenReturn(Maybe.just(IdImpl.create(3)));

        Maybe<FoodItemAddData> actual = uut.getFormData(food);

        actual.test().assertValue(FoodItemAddData.create(food.toSelection(),
                localiser.toLocalDate(clock.get().plus(food.expirationOffset())),
                ListWithSuggestion.create(locations, 1),
                ListWithSuggestion.create(units, 1)
        ));
    }

    @Test
    void missingLocationPredictorsDontBlockResult() {
        FoodForItemCreation food = FoodForItemCreation.create(1, "Banana", Period.ofDays(10), Optional.empty(), () -> 4);
        List<LocationForSelection> locations = getLocations();
        List<ScaledUnitForSelection> units = getUnits();
        when(repository.getFood(equalBy(food))).thenReturn(Maybe.just(food));
        when(repository.getLocations()).thenReturn(Maybe.just(locations));
        when(repository.getUnits()).thenReturn(Maybe.just(units));
        when(repository.getLocationWithMostItemsOfType(food)).thenReturn(Maybe.empty());

        Maybe<FoodItemAddData> actual = uut.getFormData(food);

        actual.test().assertValue(FoodItemAddData.create(food.toSelection(),
                localiser.toLocalDate(clock.get().plus(food.expirationOffset())),
                ListWithSuggestion.create(locations, 0),
                ListWithSuggestion.create(units, 1)
        ));
    }

    @Test
    void existingFoodIsQueriedForEatByDate() {
        FoodForItemCreation food = FoodForItemCreation.create(1, "Banana", Period.ZERO, Optional.of(create(3)), () -> 4);
        List<LocationForSelection> locations = getLocations();
        List<ScaledUnitForSelection> units = getUnits();
        when(repository.getFood(equalBy(food))).thenReturn(Maybe.just(food));
        when(repository.getLocations()).thenReturn(Maybe.just(locations));
        when(repository.getUnits()).thenReturn(Maybe.just(units));
        when(repository.getMaxEatByOfPresentItemsOf(food)).thenReturn(Maybe.just(Instant.EPOCH.plus(1, ChronoUnit.DAYS)));
        when(repository.getMaxEatByEverOf(food)).thenReturn(Maybe.just(Instant.EPOCH.plus(2, ChronoUnit.DAYS)));

        Maybe<FoodItemAddData> actual = uut.getFormData(food);

        actual.test().assertValue(FoodItemAddData.create(food.toSelection(),
                localiser.toLocalDate(Instant.EPOCH.plus(1, ChronoUnit.DAYS)),
                ListWithSuggestion.create(locations, 1),
                ListWithSuggestion.create(units, 1)
        ));
    }

    @Test
    void everExistedFoodIsQueriedForEatByDate() {
        FoodForItemCreation food = FoodForItemCreation.create(1, "Banana", Period.ZERO, Optional.of(create(3)), () -> 4);
        List<LocationForSelection> locations = getLocations();
        List<ScaledUnitForSelection> units = getUnits();
        when(repository.getFood(equalBy(food))).thenReturn(Maybe.just(food));
        when(repository.getLocations()).thenReturn(Maybe.just(locations));
        when(repository.getUnits()).thenReturn(Maybe.just(units));
        when(repository.getMaxEatByOfPresentItemsOf(food)).thenReturn(Maybe.empty());
        when(repository.getMaxEatByEverOf(food)).thenReturn(Maybe.just(Instant.EPOCH.plus(2, ChronoUnit.DAYS)));

        Maybe<FoodItemAddData> actual = uut.getFormData(food);

        actual.test().assertValue(FoodItemAddData.create(food.toSelection(),
                localiser.toLocalDate(Instant.EPOCH.plus(2, ChronoUnit.DAYS)),
                ListWithSuggestion.create(locations, 1),
                ListWithSuggestion.create(units, 1)
        ));
    }

    @Test
    void noPredictorResortsToNow() {
        FoodForItemCreation food = FoodForItemCreation.create(1, "Banana", Period.ZERO, Optional.of(create(3)), () -> 4);
        List<LocationForSelection> locations = getLocations();
        List<ScaledUnitForSelection> units = getUnits();
        when(repository.getFood(equalBy(food))).thenReturn(Maybe.just(food));
        when(repository.getLocations()).thenReturn(Maybe.just(locations));
        when(repository.getUnits()).thenReturn(Maybe.just(units));
        when(repository.getMaxEatByOfPresentItemsOf(food)).thenReturn(Maybe.empty());
        when(repository.getMaxEatByEverOf(food)).thenReturn(Maybe.empty());

        Maybe<FoodItemAddData> actual = uut.getFormData(food);

        actual.test().assertValue(FoodItemAddData.create(food.toSelection(),
                localiser.toLocalDate(Instant.EPOCH),
                ListWithSuggestion.create(locations, 1),
                ListWithSuggestion.create(units, 1)
        ));
    }

    @Test
    void addingFromInterfaceEnqueuesTask() {
        uut.add(getInput());

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.ADD_FOOD_ITEM, captor.getValue().name());
    }

    @Test
    void dataIsSentToService() {
        FoodItemForm localData = getInput();
        FoodItemToAdd dataToNetwork = localData.toNetwork(localiser);

        uut.addInBackground(localData);

        verify(service).add(dataToNetwork);
        verify(synchroniser).synchronise();
    }

    @Test
    void failingOperationIsRecorded() {
        FoodItemForm form = getInput();
        FoodItemToAdd expected = form.toNetwork(localiser);
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        doThrow(exception).when(service).add(expected);

        uut.addInBackground(form);

        verify(service).add(expected);
        verify(errorRecorder).recordFoodItemAddError(exception, form.toErrorRecording(localiser));
        verify(synchroniser).synchroniseAfterError(exception);
    }

    private FoodItemForm getInput() {
        return FoodItemForm.create(LocalDate.EPOCH, 1, 2, 3);
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