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
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static de.njsm.stocks.client.business.Matchers.equalBy;
import static de.njsm.stocks.client.business.entities.IdImpl.create;
import static java.util.Collections.emptyList;
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
    private EntityDeleter<EanNumber> eanNumberDeleteInteractor;

    @Mock
    private EntityDeleter<UserDevice> userDeviceDeleteInteractor;

    @Mock
    private EntityDeleter<User> userDeleteInteractor;

    @Mock
    private RecipeAddInteractor recipeAddInteractor;

    @Mock
    private FoodToBuyInteractor foodToBuyInteractor;

    @Mock
    private UserAddInteractor userAddInteractor;

    @Mock
    private UserDeviceAddInteractor userDeviceAddInteractor;

    @Mock
    private EntityDeleter<Recipe> recipeDeleteInteractor;

    @Mock
    private RecipeEditInteractor recipeEditInteractor;

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
                eanNumberDeleteInteractor,
                userDeviceDeleteInteractor,
                userDeleteInteractor,
                recipeAddInteractor,
                foodToBuyInteractor,
                userAddInteractor,
                userDeviceAddInteractor,
                recipeDeleteInteractor,
                recipeEditInteractor,
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
        FoodEditErrorDetails errorDetails = FoodEditErrorDetails.create(1, "Banana", true, Period.ofDays(3), 4, 5, "yellow");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", errorDetails);
        FoodToEdit expected = FoodToEdit.create(errorDetails.id(), errorDetails.name(), errorDetails.toBuy(), errorDetails.expirationOffset(), errorDetails.location(), errorDetails.storeUnit(), errorDetails.description());

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

        verify(foodItemDeleter).delete(equalBy(foodDeleteErrorDetails));
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

    @Test
    void retryingEanNumberDeletingDispatches() {
        EanNumberDeleteErrorDetails eanNumber = EanNumberDeleteErrorDetails.create(2, "Banana", "123");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", eanNumber);
        EanNumberForDeletion expected = EanNumberForDeletion.create(eanNumber.id(), 3);

        uut.retryInBackground(input);

        verify(eanNumberDeleteInteractor).delete(equalBy(expected));
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingUserDeviceDeletingDispatches() {
        UserDeviceDeleteErrorDetails userDevice = UserDeviceDeleteErrorDetails.create(2, "Jack", "Mobile");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", userDevice);
        UserDeviceForDeletion expected = UserDeviceForDeletion.create(userDevice.id().id(), 3);

        uut.retryInBackground(input);

        verify(userDeviceDeleteInteractor).delete(equalBy(expected));
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingUserDeletingDispatches() {
        UserDeleteErrorDetails user = UserDeleteErrorDetails.create(2, "Jack");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", user);
        UserForDeletion expected = UserForDeletion.create(user.id().id(), 3);

        uut.retryInBackground(input);

        verify(userDeleteInteractor).delete(equalBy(expected));
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingRecipeAddDispatches() {
        var recipe = RecipeAddForm.create("Pizza", "just bake", Duration.ofMinutes(3), emptyList(), emptyList());
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", recipe);

        uut.retryInBackground(input);

        verify(recipeAddInteractor).add(recipe);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingFoodBuyingDispatches() {
        var food = FoodForBuying.create(1, 2, true);
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", food);

        uut.retryInBackground(input);

        verify(foodToBuyInteractor).manageFoodToBuy(FoodToBuy.putOnShoppingList(food.id()));
        verify(errorRepository).deleteError(input);
    }

    @Test
    void userAddingDispatches() {
        var user = UserAddForm.create("Joanna");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", user);

        uut.retryInBackground(input);

        verify(userAddInteractor).add(user);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void userDeviceAddingDispatches() {
        var userDevice = UserDeviceAddErrorDetails.create("Mobile", IdImpl.create(42), "John");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", userDevice);

        uut.retryInBackground(input);

        verify(userDeviceAddInteractor).add(userDevice.into());
        verify(errorRepository).deleteError(input);
    }

    @Test
    void recipeDeletingDispatches() {
        var recipe = RecipeDeleteErrorDetails.create(42, "Pizza");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", recipe);

        uut.retryInBackground(input);

        verify(recipeDeleteInteractor).delete(recipe);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void recipeEditingDispatches() {
        RecipeEditForm recipe = RecipeEditForm.create(
                RecipeEditBaseData.create(1, "Pizza", "just bake", Duration.ofMinutes(2)),
                List.of(RecipeIngredientEditFormData.create(
                        3, 4, -1, create(5), -1, create(6)
                )),
                List.of(RecipeProductEditFormData.create(
                        7, 8, -1, create(9), -1, create(10)
                )));
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", recipe);

        uut.retryInBackground(input);

        verify(recipeEditInteractor).edit(recipe);
        verify(errorRepository).deleteError(input);
    }
}
