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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.List;

import static java.util.Collections.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SynchroniseInteractorImplTest {

    @Mock
    UpdateService updateService;

    @Mock
    SynchronisationRepository synchronisationRepository;

    @Mock
    ErrorRecorder errorRecorder;

    private SynchroniseInteractor uut;

    @BeforeEach
    void setUp() {
        uut = new SynchroniseInteractorImpl(updateService, synchronisationRepository, errorRecorder);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(synchronisationRepository);
        verifyNoMoreInteractions(updateService);
        verifyNoMoreInteractions(errorRecorder);
    }

    @Nested
    class RecipeProductSynchronisation extends TestSkeleton<RecipeProductForSynchronisation> {

        @Override
        RecipeProductForSynchronisation getEntity() {
            return initialiseEntity(RecipeProductForSynchronisation.builder())
                    .amount(4)
                    .product(5)
                    .recipe(6)
                    .unit(7)
                    .build();
        }

        @Override
        EntityType getEntityType() {
            return EntityType.RECIPE_PRODUCT;
        }

        @Override
        void prepareMocks(List<RecipeProductForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            when(updateService.getRecipeProducts(startingFrom, upUntil)).thenReturn(entities);
        }

        @Override
        void verifyInitialisationMocks(List<RecipeProductForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getRecipeProducts(startingFrom, upUntil);
            verify(synchronisationRepository).initialiseRecipeProducts(entities);

        }

        @Override
        void verifyMocks(List<RecipeProductForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getRecipeProducts(startingFrom, upUntil);
            verify(synchronisationRepository).writeRecipeProducts(entities);
        }
    }

    @Nested
    class RecipeIngredientSynchronisation extends TestSkeleton<RecipeIngredientForSynchronisation> {

        @Override
        RecipeIngredientForSynchronisation getEntity() {
            return initialiseEntity(RecipeIngredientForSynchronisation.builder())
                    .amount(4)
                    .ingredient(5)
                    .recipe(6)
                    .unit(7)
                    .build();
        }

        @Override
        EntityType getEntityType() {
            return EntityType.RECIPE_INGREDIENT;
        }

        @Override
        void prepareMocks(List<RecipeIngredientForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            when(updateService.getRecipeIngredients(startingFrom, upUntil)).thenReturn(entities);
        }

        @Override
        void verifyInitialisationMocks(List<RecipeIngredientForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getRecipeIngredients(startingFrom, upUntil);
            verify(synchronisationRepository).initialiseRecipeIngredients(entities);

        }

        @Override
        void verifyMocks(List<RecipeIngredientForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getRecipeIngredients(startingFrom, upUntil);
            verify(synchronisationRepository).writeRecipeIngredients(entities);
        }
    }

    @Nested
    class RecipeSynchronisation extends TestSkeleton<RecipeForSynchronisation> {

        @Override
        RecipeForSynchronisation getEntity() {
            return initialiseEntity(RecipeForSynchronisation.builder())
                    .name("name")
                    .instructions("instructions")
                    .duration(Duration.ofDays(4))
                    .build();
        }

        @Override
        EntityType getEntityType() {
            return EntityType.RECIPE;
        }

        @Override
        void prepareMocks(List<RecipeForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            when(updateService.getRecipes(startingFrom, upUntil)).thenReturn(entities);
        }

        @Override
        void verifyInitialisationMocks(List<RecipeForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getRecipes(startingFrom, upUntil);
            verify(synchronisationRepository).initialiseRecipes(entities);

        }

        @Override
        void verifyMocks(List<RecipeForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getRecipes(startingFrom, upUntil);
            verify(synchronisationRepository).writeRecipes(entities);
        }
    }

    @Nested
    class ScaledUnitSynchronisation extends TestSkeleton<ScaledUnitForSynchronisation> {

        @Override
        ScaledUnitForSynchronisation getEntity() {
            return initialiseEntity(ScaledUnitForSynchronisation.builder())
                    .scale(BigDecimal.TEN)
                    .unit(4)
                    .build();
        }

        @Override
        EntityType getEntityType() {
            return EntityType.SCALED_UNIT;
        }

        @Override
        void prepareMocks(List<ScaledUnitForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            when(updateService.getScaledUnits(startingFrom, upUntil)).thenReturn(entities);
        }

        @Override
        void verifyInitialisationMocks(List<ScaledUnitForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getScaledUnits(startingFrom, upUntil);
            verify(synchronisationRepository).initialiseScaledUnits(entities);

        }

        @Override
        void verifyMocks(List<ScaledUnitForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getScaledUnits(startingFrom, upUntil);
            verify(synchronisationRepository).writeScaledUnits(entities);
        }
    }

    @Nested
    class UnitSynchronisation extends TestSkeleton<UnitForSynchronisation> {

        @Override
        UnitForSynchronisation getEntity() {
            return initialiseEntity(UnitForSynchronisation.builder())
                    .name("name")
                    .abbreviation("abbreviation")
                    .build();
        }

        @Override
        EntityType getEntityType() {
            return EntityType.UNIT;
        }

        @Override
        void prepareMocks(List<UnitForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            when(updateService.getUnits(startingFrom, upUntil)).thenReturn(entities);
        }

        @Override
        void verifyInitialisationMocks(List<UnitForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getUnits(startingFrom, upUntil);
            verify(synchronisationRepository).initialiseUnits(entities);

        }

        @Override
        void verifyMocks(List<UnitForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getUnits(startingFrom, upUntil);
            verify(synchronisationRepository).writeUnits(entities);
        }
    }

    @Nested
    class FoodItemSynchronisation extends TestSkeleton<FoodItemForSynchronisation> {

        @Override
        FoodItemForSynchronisation getEntity() {
            return initialiseEntity(FoodItemForSynchronisation.builder())
                    .eatBy(Instant.EPOCH)
                    .ofType(4)
                    .storedIn(5)
                    .buys(6)
                    .registers(7)
                    .unit(8)
                    .build();
        }

        @Override
        EntityType getEntityType() {
            return EntityType.FOOD_ITEM;
        }

        @Override
        void prepareMocks(List<FoodItemForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            when(updateService.getFoodItems(startingFrom, upUntil)).thenReturn(entities);
        }

        @Override
        void verifyInitialisationMocks(List<FoodItemForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getFoodItems(startingFrom, upUntil);
            verify(synchronisationRepository).initialiseFoodItems(entities);

        }

        @Override
        void verifyMocks(List<FoodItemForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getFoodItems(startingFrom, upUntil);
            verify(synchronisationRepository).writeFoodItems(entities);
        }
    }

    @Nested
    class EanNumberSynchronisation extends TestSkeleton<EanNumberForSynchronisation> {

        @Override
        EanNumberForSynchronisation getEntity() {
            return initialiseEntity(EanNumberForSynchronisation.builder())
                    .number("number")
                    .identifies(4)
                    .build();
        }

        @Override
        EntityType getEntityType() {
            return EntityType.EAN_NUMBER;
        }

        @Override
        void prepareMocks(List<EanNumberForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            when(updateService.getEanNumbers(startingFrom, upUntil)).thenReturn(entities);
        }

        @Override
        void verifyInitialisationMocks(List<EanNumberForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getEanNumbers(startingFrom, upUntil);
            verify(synchronisationRepository).initialiseEanNumbers(entities);

        }

        @Override
        void verifyMocks(List<EanNumberForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getEanNumbers(startingFrom, upUntil);
            verify(synchronisationRepository).writeEanNumbers(entities);
        }
    }

    @Nested
    class FoodSynchronisation extends TestSkeleton<FoodForSynchronisation> {

        @Override
        FoodForSynchronisation getEntity() {
            return initialiseEntity(FoodForSynchronisation.builder())
                    .name("name")
                    .toBuy(true)
                    .expirationOffset(Period.ofDays(4))
                    .location(5)
                    .storeUnit(6)
                    .description("description")
                    .build();
        }

        @Override
        EntityType getEntityType() {
            return EntityType.FOOD;
        }

        @Override
        void prepareMocks(List<FoodForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            when(updateService.getFood(startingFrom, upUntil)).thenReturn(entities);
        }

        @Override
        void verifyInitialisationMocks(List<FoodForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getFood(startingFrom, upUntil);
            verify(synchronisationRepository).initialiseFood(entities);

        }

        @Override
        void verifyMocks(List<FoodForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getFood(startingFrom, upUntil);
            verify(synchronisationRepository).writeFood(entities);
        }
    }

    @Nested
    class UserSynchronisation extends TestSkeleton<UserForSynchronisation> {

        @Override
        UserForSynchronisation getEntity() {
            return initialiseEntity(UserForSynchronisation.builder())
                    .name("name")
                    .build();
        }

        @Override
        EntityType getEntityType() {
            return EntityType.USER;
        }

        @Override
        void prepareMocks(List<UserForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            when(updateService.getUsers(startingFrom, upUntil)).thenReturn(entities);
        }

        @Override
        void verifyInitialisationMocks(List<UserForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getUsers(startingFrom, upUntil);
            verify(synchronisationRepository).initialiseUsers(entities);

        }

        @Override
        void verifyMocks(List<UserForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getUsers(startingFrom, upUntil);
            verify(synchronisationRepository).writeUsers(entities);
        }
    }

    @Nested
    class UserDeviceSynchronisation extends TestSkeleton<UserDeviceForSynchronisation> {

        @Override
        UserDeviceForSynchronisation getEntity() {
            return initialiseEntity(UserDeviceForSynchronisation.builder())
                    .name("name")
                    .belongsTo(4)
                    .build();
        }

        @Override
        EntityType getEntityType() {
            return EntityType.USER_DEVICE;
        }

        @Override
        void prepareMocks(List<UserDeviceForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            when(updateService.getUserDevices(startingFrom, upUntil)).thenReturn(entities);
        }

        @Override
        void verifyInitialisationMocks(List<UserDeviceForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getUserDevices(startingFrom, upUntil);
            verify(synchronisationRepository).initialiseUserDevices(entities);

        }

        @Override
        void verifyMocks(List<UserDeviceForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getUserDevices(startingFrom, upUntil);
            verify(synchronisationRepository).writeUserDevices(entities);
        }
    }

    @Nested
    class LocationSynchronisation extends TestSkeleton<LocationForSynchronisation> {

        @Override
        LocationForSynchronisation getEntity() {
            return initialiseEntity(LocationForSynchronisation.builder())
                    .name("name")
                    .description("description")
                    .build();
        }

        @Override
        EntityType getEntityType() {
            return EntityType.LOCATION;
        }

        @Override
        void prepareMocks(List<LocationForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            when(updateService.getLocations(startingFrom, upUntil)).thenReturn(entities);
        }

        @Override
        void verifyInitialisationMocks(List<LocationForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getLocations(startingFrom, upUntil);
            verify(synchronisationRepository).initialiseLocations(entities);
        }

        @Override
        void verifyMocks(List<LocationForSynchronisation> entities, Instant startingFrom, Instant upUntil) {
            verify(updateService).getLocations(startingFrom, upUntil);
            verify(synchronisationRepository).writeLocations(entities);
        }
    }

    abstract class TestSkeleton<E> {

        abstract E getEntity();

        abstract EntityType getEntityType();

        abstract void prepareMocks(List<E> entities, Instant startingFrom, Instant upUntil);

        abstract void verifyInitialisationMocks(List<E> entities, Instant startingFrom, Instant upUntil);

        abstract void verifyMocks(List<E> entities, Instant startingFrom, Instant upUntil);

        <T extends Bitemporal.Builder<T>> T initialiseEntity(T builder) {
            return builder
                    .id(1)
                    .version(2)
                    .validTimeStart(Instant.EPOCH)
                    .validTimeEnd(Constants.INFINITY)
                    .transactionTimeStart(Instant.EPOCH)
                    .transactionTimeEnd(Constants.INFINITY)
                    .initiates(3);
        }

        @Test
        void synchronisesIfServerHasMoreRecentData() {
            Update localUpdate = Update.create(getEntityType(), Instant.EPOCH);
            Update serverUpdate = Update.create(localUpdate.table(), localUpdate.lastUpdate().plusSeconds(1));
            when(updateService.getUpdates()).thenReturn(singletonList(serverUpdate));
            when(synchronisationRepository.getUpdates()).thenReturn(singletonList(localUpdate));
            List<E> entities = singletonList(getEntity());
            prepareMocks(entities, localUpdate.lastUpdate(), serverUpdate.lastUpdate());

            uut.synchronise();

            verify(updateService).getUpdates();
            verify(synchronisationRepository).getUpdates();
            verifyMocks(entities, localUpdate.lastUpdate(), serverUpdate.lastUpdate());
            verify(synchronisationRepository).writeUpdates(singletonList(serverUpdate));
        }

        @Test
        void initialisesDataIfNoLocalUpdatePresent() {
            Update serverUpdate = Update.create(getEntityType(), Instant.EPOCH);
            when(updateService.getUpdates()).thenReturn(singletonList(serverUpdate));
            when(synchronisationRepository.getUpdates()).thenReturn(emptyList());
            List<E> entities = singletonList(getEntity());
            prepareMocks(entities, Instant.MIN, serverUpdate.lastUpdate());

            uut.synchronise();

            verify(updateService).getUpdates();
            verify(synchronisationRepository).getUpdates();
            verifyInitialisationMocks(entities, Instant.MIN, serverUpdate.lastUpdate());
            verify(synchronisationRepository).writeUpdates(singletonList(serverUpdate));
        }
    }

    @Test
    void doesntSynchroniseIfNoServerChanges() {
        List<Update> commonlyAgreedState = singletonList(Update.create(EntityType.LOCATION, Instant.EPOCH));
        when(updateService.getUpdates()).thenReturn(commonlyAgreedState);
        when(synchronisationRepository.getUpdates()).thenReturn(commonlyAgreedState);

        uut.synchronise();

        verify(updateService).getUpdates();
        verify(synchronisationRepository).getUpdates();
        verify(synchronisationRepository).writeUpdates(commonlyAgreedState);
    }

    @Test
    void exceptionDuringGettingUpdatesIsRecorded() {
        StatusCodeException expected = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        when(updateService.getUpdates()).thenThrow(expected);

        uut.synchronise();

        verify(errorRecorder).recordSynchronisationError(expected);
        verify(updateService).getUpdates();
    }
}
