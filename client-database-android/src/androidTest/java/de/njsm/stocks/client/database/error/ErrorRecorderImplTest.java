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

package de.njsm.stocks.client.database.error;

import de.njsm.stocks.client.business.ErrorRecorder;
import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.SubsystemException;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.database.DbTestCase;
import de.njsm.stocks.client.database.PreservedId;
import de.njsm.stocks.client.database.UpdateDbEntity;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ErrorRecorderImplTest extends DbTestCase {

    private ErrorRecorder uut;

    @Before
    public void setup() {
        uut = new ErrorRecorderImpl(stocksDatabase.errorDao(), this);

        List<UpdateDbEntity> updates = Arrays.stream(EntityType.values())
                .map(v -> UpdateDbEntity.create(v, Instant.ofEpochSecond(randomnessProvider.getId(v + " update time"))))
                .collect(Collectors.toList());
        stocksDatabase.synchronisationDao().insert(updates);
    }

    @Test
    public void recordingStatusCodeExceptionWorks() {
        StatusCodeException input = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        uut.recordSynchronisationError(input);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity actual = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(input.getStatusCode(), actual.statusCode());
        assertNotEquals(0, actual.id());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.SYNCHRONISATION, errors.get(0).action());
        assertEquals(0, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingSubsystemExceptionWorks() {
        SubsystemException input = new SubsystemException("message");

        uut.recordSynchronisationError(input);

        List<SubsystemExceptionEntity> recordedErrors = stocksDatabase.errorDao().getSubsystemErrors();
        assertEquals(1, recordedErrors.size());
        SubsystemExceptionEntity actual = recordedErrors.get(0);
        assertEquals(input.getMessage(), actual.message());
        assertNotEquals(0, actual.id());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.SYNCHRONISATION, errors.get(0).action());
        assertEquals(0, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.SUBSYSTEM_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingErrorAddingLocationWorks() {
        LocationAddForm form = LocationAddForm.create("Fridge", "the cold one");
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        uut.recordLocationAddError(exception, form);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity actual = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(exception.getStatusCode(), actual.statusCode());
        List<LocationAddEntity> locationAddEntities = stocksDatabase.errorDao().getLocationAdds();
        assertEquals(1, locationAddEntities.size());
        assertEquals(form.name(), locationAddEntities.get(0).name());
        assertEquals(form.description(), locationAddEntities.get(0).description());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.ADD_LOCATION, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingErrorDeletingLocationWorks() {
        LocationForDeletion locationForDeletion = LocationForDeletion.builder()
                .id(2)
                .version(3)
                .build();
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        uut.recordLocationDeleteError(exception, locationForDeletion);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity actual = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(exception.getStatusCode(), actual.statusCode());
        List<LocationDeleteEntity> locationDeleteEntities = stocksDatabase.errorDao().getLocationDeletes();
        assertEquals(1, locationDeleteEntities.size());
        LocationDeleteEntity locationDelete = locationDeleteEntities.get(0);
        assertEquals(1, locationDelete.id());
        assertEquals(locationForDeletion.id(), locationDelete.location().id());
        assertEquals(locationForDeletion.version(), locationDelete.version());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.LOCATION), locationDelete.location().transactionTime());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.DELETE_LOCATION, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingErrorEditingLocationWorks() {
        LocationForEditing locationForEditing = LocationForEditing.builder()
                .id(2)
                .version(3)
                .name("name")
                .description("description")
                .build();
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        uut.recordLocationEditError(exception, locationForEditing);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity actual = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(exception.getStatusCode(), actual.statusCode());
        List<LocationEditEntity> locationEditEntities = stocksDatabase.errorDao().getLocationEdits();
        assertEquals(1, locationEditEntities.size());
        LocationEditEntity locationEditEntity = locationEditEntities.get(0);
        assertEquals(1, locationEditEntity.id());
        assertEquals(locationForEditing.id(), locationEditEntity.location().id());
        assertEquals(locationForEditing.version(), locationEditEntity.version());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.LOCATION), locationEditEntity.location().transactionTime());
        assertEquals(locationForEditing.name(), locationEditEntity.name());
        assertEquals(locationForEditing.description(), locationEditEntity.description());
        assertEquals(getNow(), locationEditEntity.executionTime());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.EDIT_LOCATION, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingErrorAddingUnitWorks() {
        UnitAddForm form = UnitAddForm.create("Gramm", "g");
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        uut.recordUnitAddError(exception, form);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity actual = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(exception.getStatusCode(), actual.statusCode());
        List<UnitAddEntity> unitAddEntities = stocksDatabase.errorDao().getUnitAdds();
        assertEquals(1, unitAddEntities.size());
        assertEquals(form.name(), unitAddEntities.get(0).name());
        assertEquals(form.abbreviation(), unitAddEntities.get(0).abbreviation());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.ADD_UNIT, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingErrorDeletingUnitWorks() {
        UnitForDeletion unitForDeletion = UnitForDeletion.builder()
                .id(2)
                .version(3)
                .build();
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        uut.recordUnitDeleteError(exception, unitForDeletion);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity actual = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(exception.getStatusCode(), actual.statusCode());
        List<UnitDeleteEntity> unitDeleteEntities = stocksDatabase.errorDao().getUnitDeletes();
        assertEquals(1, unitDeleteEntities.size());
        UnitDeleteEntity unitDeleteEntity = unitDeleteEntities.get(0);
        assertEquals(1, unitDeleteEntity.id());
        assertEquals(unitForDeletion.id(), unitDeleteEntity.unit().id());
        assertEquals(unitForDeletion.version(), unitDeleteEntity.version());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.UNIT), unitDeleteEntity.unit().transactionTime());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.DELETE_UNIT, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingErrorEditingUnitWorks() {
        UnitForEditing unitForEditing = UnitForEditing.builder()
                .id(2)
                .version(3)
                .name("name")
                .abbreviation("abbreviation")
                .build();
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        uut.recordUnitEditError(exception, unitForEditing);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity actual = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(exception.getStatusCode(), actual.statusCode());
        List<UnitEditEntity> unitEditEntities = stocksDatabase.errorDao().getUnitEdits();
        assertEquals(1, unitEditEntities.size());
        UnitEditEntity unitEditEntity = unitEditEntities.get(0);
        assertEquals(1, unitEditEntity.id());
        assertEquals(unitForEditing.id(), unitEditEntity.unit().id());
        assertEquals(unitForEditing.version(), unitEditEntity.version());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.UNIT), unitEditEntity.unit().transactionTime());
        assertEquals(unitForEditing.name(), unitEditEntity.name());
        assertEquals(unitForEditing.abbreviation(), unitEditEntity.abbreviation());
        assertEquals(getNow(), unitEditEntity.executionTime());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.EDIT_UNIT, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingErrorAddingScaledUnitWorks() {
        ScaledUnitAddForm form = ScaledUnitAddForm.create(BigDecimal.ONE, 2);
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        uut.recordScaledUnitAddError(exception, form);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity actual = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(exception.getStatusCode(), actual.statusCode());
        List<ScaledUnitAddEntity> scaledUnitAdds = stocksDatabase.errorDao().getScaledUnitAdds();
        assertEquals(1, scaledUnitAdds.size());
        assertEquals(form.scale(), scaledUnitAdds.get(0).scale());
        assertEquals(form.unit(), scaledUnitAdds.get(0).unit().id());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.UNIT), scaledUnitAdds.get(0).unit().transactionTime());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.ADD_SCALED_UNIT, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingErrorEditingScaledUnitWorks() {
        ScaledUnitForEditing form = ScaledUnitForEditing.create(1, 2, BigDecimal.valueOf(3), 4);
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        uut.recordScaledUnitEditError(exception, form);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity actual = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(exception.getStatusCode(), actual.statusCode());
        List<ScaledUnitEditEntity> scaledUnitEdits = stocksDatabase.errorDao().getScaledUnitEdits();
        assertEquals(1, scaledUnitEdits.size());
        ScaledUnitEditEntity scaledUnitEditEntity = scaledUnitEdits.get(0);
        assertEquals(form.id(), scaledUnitEditEntity.scaledUnit().id());
        assertEquals(form.version(), scaledUnitEditEntity.version());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.SCALED_UNIT), scaledUnitEditEntity.scaledUnit().transactionTime());
        assertEquals(getNow(), scaledUnitEditEntity.executionTime());
        assertEquals(form.scale(), scaledUnitEditEntity.scale());
        assertEquals(form.unit(), scaledUnitEditEntity.unit().id());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.UNIT), scaledUnitEditEntity.unit().transactionTime());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.EDIT_SCALED_UNIT, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingErrorDeletingScaledUnitWorks() {
        ScaledUnitForDeletion form = ScaledUnitForDeletion.create(1, 2);
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        uut.recordScaledUnitDeleteError(exception, form);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity actual = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(exception.getStatusCode(), actual.statusCode());
        List<ScaledUnitDeleteEntity> scaledUnitDeletes = stocksDatabase.errorDao().getScaledUnitDeletes();
        assertEquals(1, scaledUnitDeletes.size());
        ScaledUnitDeleteEntity scaledUnitDeleteEntity = scaledUnitDeletes.get(0);
        assertEquals(form.id(), scaledUnitDeleteEntity.scaledUnit().id());
        assertEquals(form.version(), scaledUnitDeleteEntity.version());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.SCALED_UNIT), scaledUnitDeleteEntity.scaledUnit().transactionTime());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.DELETE_SCALED_UNIT, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingErrorAddingFoodWorks() {
        FoodAddForm form = FoodAddForm.create("Banana", true, Period.ZERO, null, 1, "they are yellow");
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        uut.recordFoodAddError(exception, form);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity actual = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(exception.getStatusCode(), actual.statusCode());
        List<FoodAddEntity> foodAdds = stocksDatabase.errorDao().getFoodAdds();
        assertEquals(1, foodAdds.size());
        FoodAddEntity foodAddEntity = foodAdds.get(0);
        assertEquals(form.name(), foodAddEntity.name());
        assertEquals(form.toBuy(), foodAddEntity.toBuy());
        assertEquals(form.expirationOffset(), foodAddEntity.expirationOffset());
        assertNull(foodAddEntity.location().id());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.LOCATION), foodAddEntity.location().transactionTime());
        assertEquals(form.storeUnit(), foodAddEntity.storeUnit().id());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.SCALED_UNIT), foodAddEntity.storeUnit().transactionTime());
        assertEquals(form.description(), foodAddEntity.description());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.ADD_FOOD, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingErrorDeletingFoodWorks() {
        test(
                FoodForDeletion.create(2, 3),
                uut::recordFoodDeleteError,
                ErrorEntity.Action.DELETE_FOOD,
                stocksDatabase.errorDao()::getFoodDeletes,
                (expected, actual) -> {
                    assertEquals(expected.id(), actual.food().id());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.FOOD), actual.food().transactionTime());
                    assertEquals(expected.version(), actual.version());
                }
        );
    }

    @Test
    public void recordingErrorEditingFoodWorks() {
        test(
                FoodForEditing.create(1, 2, "Banana", true, Period.ofDays(3), Optional.of(4), 5, "yellow"),
                uut::recordFoodEditError,
                ErrorEntity.Action.EDIT_FOOD,
                stocksDatabase.errorDao()::getFoodEdits,
                (form, foodEditEntity)  -> {
                    assertEquals(form.id(), foodEditEntity.food().id());
                    assertEquals(form.version(), foodEditEntity.version());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.FOOD), foodEditEntity.food().transactionTime());
                    assertEquals(getNow(), foodEditEntity.executionTime());
                    assertEquals(form.name(), foodEditEntity.name());
                    assertEquals(form.expirationOffset(), foodEditEntity.expirationOffset());
                    assertEquals(form.location(), foodEditEntity.location().maybe().map(PreservedId::id));
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.LOCATION), foodEditEntity.location().transactionTime());
                    assertEquals(form.storeUnit(), foodEditEntity.storeUnit().id());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.SCALED_UNIT), foodEditEntity.storeUnit().transactionTime());
                    assertEquals(form.description(), foodEditEntity.description());
                    assertEquals(getNow(), foodEditEntity.executionTime());
                }
        );
    }

    @Test
    public void recordingErrorAddingFoodItemWorks() {
        test(FoodItemAddFormForErrorRecording.create(Instant.ofEpochMilli(2), 1, 2, 3),
                uut::recordFoodItemAddError,
                ErrorEntity.Action.ADD_FOOD_ITEM,
                stocksDatabase.errorDao()::getFoodItemAdds,
                (expected, actual) -> {
                    assertEquals(expected.eatBy(), actual.eatBy());
                    assertEquals(expected.ofType(), actual.ofType().id());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.FOOD), actual.ofType().transactionTime());
                    assertEquals(expected.storedIn(), actual.storedIn().id());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.LOCATION), actual.storedIn().transactionTime());
                    assertEquals(expected.unit(), actual.unit().id());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.SCALED_UNIT), actual.unit().transactionTime());
            }
        );
    }

    @Test
    public void recordingErrorDeletingFoodItemWorks() {
        test(FoodItemForDeletion.create(1, 2),
                uut::recordFoodItemDeleteError,
                ErrorEntity.Action.DELETE_FOOD_ITEM,
                stocksDatabase.errorDao()::getFoodItemDeletes,
                (expected, actual) -> {
                    assertEquals(expected.id(), actual.foodItem().id());
                    assertEquals(expected.version(), actual.version());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.FOOD_ITEM), actual.foodItem().transactionTime());
                }
        );
    }

    @Test
    public void recordingErrorEditingFoodItemWorks() {
        test(FoodItemForEditing.create(1, 2, Instant.EPOCH, 3, 4),
                uut::recordFoodItemEditError,
                ErrorEntity.Action.EDIT_FOOD_ITEM,
                stocksDatabase.errorDao()::getFoodItemEdits,
                (expected, actual) -> {
                    assertEquals(expected.id(), actual.foodItem().id());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.FOOD_ITEM), actual.foodItem().transactionTime());
                    assertEquals(expected.eatBy(), actual.eatBy());
                    assertEquals(expected.storedIn(), actual.storedIn().id());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.LOCATION), actual.storedIn().transactionTime());
                    assertEquals(expected.unit(), actual.unit().id());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.SCALED_UNIT), actual.unit().transactionTime());
                    assertEquals(getNow(), actual.executionTime());
                }
        );
    }

    @Test
    public void recordingErrorAddingEanNumberWorks() {
        test(EanNumberAddForm.create(() -> 42, "123"),
                uut::recordEanNumberAddError,
                ErrorEntity.Action.ADD_EAN_NUMBER,
                stocksDatabase.errorDao()::getEanNumberAdds,
                (expected, actual) -> {
                    assertEquals(expected.eanNumber(), actual.eanNumber());
                    assertEquals(expected.identifies().id(), actual.identifies().id());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.FOOD), actual.identifies().transactionTime());
                }
        );
    }

    @Test
    public void recordingErrorDeletingEanNumberWorks() {
        test(EanNumberForDeletion.create(1, 2),
                uut::recordEanNumberDeleteError,
                ErrorEntity.Action.DELETE_EAN_NUMBER,
                stocksDatabase.errorDao()::getEanNumberDeletes,
                (expected, actual) -> {
                    assertEquals(expected.id(), actual.eanNumber().id());
                    assertEquals(expected.version(), actual.version());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.EAN_NUMBER), actual.eanNumber().transactionTime());
                }
        );
    }

    @Test
    public void recordingErrorDeletingUserDeviceWorks() {
        test(UserDeviceForDeletion.create(1, 2),
                uut::recordUserDeviceDeleteError,
                ErrorEntity.Action.DELETE_USER_DEVICE,
                stocksDatabase.errorDao()::getUserDeviceDeletes,
                (expected, actual) -> {
                    assertEquals(expected.id(), actual.userDevice().id());
                    assertEquals(expected.version(), actual.version());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.USER_DEVICE), actual.userDevice().transactionTime());
                }
        );
    }

    @Test
    public void recordingErrorDeletingUserWorks() {
        test(UserForDeletion.create(1, 2),
                uut::recordUserDeleteError,
                ErrorEntity.Action.DELETE_USER,
                stocksDatabase.errorDao()::getUserDeletes,
                (expected, actual) -> {
                    assertEquals(expected.id(), actual.user().id());
                    assertEquals(expected.version(), actual.version());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.USER), actual.user().transactionTime());
                }
        );
    }

    @Test
    public void recordingErrorPuttingFoodToBuyWorks() {
        test(FoodForBuying.create(1, 2, true),
                uut::recordFoodToBuyError,
                ErrorEntity.Action.FOOD_SHOPPING,
                stocksDatabase.errorDao()::getFoodToBuy,
                (expected, actual) -> {
                    assertEquals(expected.id(), actual.food().id());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.FOOD), actual.food().transactionTime());
                    assertEquals(expected.version(), actual.version());
                    assertEquals(expected.toBuy(), actual.toBuy());
                    assertEquals(getNow(), actual.executionTime());
                }
        );
    }

    @Test
    public void recordingErrorAddingUserWorks() {
        test(UserAddForm.create("Joanna"),
                uut::recordUserAddError,
                ErrorEntity.Action.ADD_USER,
                stocksDatabase.errorDao()::getUserAdds,
                (expected, actual) -> {
                    assertEquals(expected.name(), actual.name());
                }
        );
    }

    @Test
    public void recordingErrorAddingUserDeviceWorks() {
        test(UserDeviceAddForm.create("Mobile", IdImpl.create(42)),
                uut::recordUserDeviceAddError,
                ErrorEntity.Action.ADD_USER_DEVICE,
                stocksDatabase.errorDao()::getUserDeviceAdds,
                (expected, actual) -> {
                    assertEquals(expected.name(), actual.name());
                    assertEquals(expected.owner().id(), actual.belongsTo().id());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.USER), actual.belongsTo().transactionTime());
                }
        );
    }

    @Test
    public void recordingErrorDeletingRecipeWorks() {
        test(IdImpl.create(42),
                uut::recordRecipeDeleteError,
                ErrorEntity.Action.DELETE_RECIPE,
                stocksDatabase.errorDao()::getRecipeDeletes,
                (expected, actual) -> {
                    assertEquals(expected.id(), actual.recipe().id());
                    assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.RECIPE), actual.recipe().transactionTime());
                }
        );
    }

    private <T, E> void test(T input,
                             BiConsumer<? super SubsystemException, T> recorder,
                             ErrorEntity.Action action,
                             Provider<List<E>> entityLoader,
                             BiConsumer<T, E> verifier) {
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        recorder.accept(exception, input);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity exceptionEntity = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(exception.getStatusCode(), exceptionEntity.statusCode());
        List<E> entities = entityLoader.get();
        assertEquals(1, entities.size());
        E actual = entities.get(0);
        verifier.accept(input, actual);
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(action, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }

    @Test
    public void recordingErrorAddingRecipeWorks() {
        RecipeAddForm input = RecipeAddForm.create("Pizza", "just bake", Duration.ofMinutes(5),
                List.of(RecipeIngredientToAdd.create(1, IdImpl.create(2), IdImpl.create(3))),
                List.of(RecipeProductToAdd.create(4, IdImpl.create(5), IdImpl.create(6))));

        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);

        uut.recordRecipeAddError(exception, input);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity exceptionEntity = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(exception.getStatusCode(), exceptionEntity.statusCode());

        List<RecipeAddEntity> recipes = stocksDatabase.errorDao().getRecipeAdds();
        assertEquals(1, recipes.size());
        RecipeAddEntity actual = recipes.get(0);
        assertEquals(input.name(), actual.name());
        assertEquals(input.instructions(), actual.instructions());
        assertEquals(input.duration(), actual.duration());

        List<RecipeIngredientAddEntity> recipeIngredients = stocksDatabase.errorDao().getRecipeIngredientAdds();
        assertEquals(1, recipeIngredients.size());
        RecipeIngredientAddEntity actualIngredient = recipeIngredients.get(0);
        RecipeIngredientToAdd expectedIngredient = input.ingredients().get(0);
        assertEquals(expectedIngredient.amount(), actualIngredient.amount());
        assertEquals(expectedIngredient.ingredient().id(), actualIngredient.ingredient().id());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.FOOD), actualIngredient.ingredient().transactionTime());
        assertEquals(expectedIngredient.unit().id(), actualIngredient.unit().id());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.SCALED_UNIT), actualIngredient.unit().transactionTime());
        assertEquals(actual.id(), actualIngredient.recipeToAdd());

        List<RecipeProductAddEntity> recipeProducts = stocksDatabase.errorDao().getRecipeProductAdds();
        assertEquals(1, recipeProducts.size());
        RecipeProductAddEntity actualProduct = recipeProducts.get(0);
        RecipeProductToAdd expectedProduct = input.products().get(0);
        assertEquals(expectedProduct.amount(), actualProduct.amount());
        assertEquals(expectedProduct.product().id(), actualProduct.product().id());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.FOOD), actualProduct.product().transactionTime());
        assertEquals(expectedProduct.unit().id(), actualProduct.unit().id());
        assertEquals(stocksDatabase.errorDao().getTransactionTimeOf(EntityType.SCALED_UNIT), actualProduct.unit().transactionTime());
        assertEquals(actual.id(), actualProduct.recipeToAdd());

        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.ADD_RECIPE, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }
}
