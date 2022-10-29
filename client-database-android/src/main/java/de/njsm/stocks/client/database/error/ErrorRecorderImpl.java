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

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.Clock;
import de.njsm.stocks.client.business.ErrorRecorder;
import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.SubsystemException;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.database.NullablePreservedId;
import de.njsm.stocks.client.database.PreservedId;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

import static de.njsm.stocks.client.database.DataMapper.map;
import static java.util.Optional.ofNullable;

public class ErrorRecorderImpl implements ErrorRecorder {

    private final ErrorDao errorDao;

    private final Clock clock;

    @Inject
    ErrorRecorderImpl(ErrorDao errorDao, Clock clock) {
        this.errorDao = errorDao;
        this.clock = clock;
    }

    @Override
    public void recordSynchronisationError(SubsystemException input) {
        ExceptionData exceptionData = new ExceptionInserter().visit(input, null);
        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.SYNCHRONISATION, 0, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordLocationAddError(SubsystemException exception, LocationAddForm form) {
        ExceptionData exceptionData = new ExceptionInserter().visit(exception, null);

        LocationAddEntity locationAddEntity = map(form);
        long dataId = errorDao.insert(locationAddEntity);

        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.ADD_LOCATION, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordLocationDeleteError(SubsystemException exception, Versionable<Location> locationForDeletion) {
        ExceptionData exceptionData = new ExceptionInserter().visit(exception, null);

        Instant currentTransactionTime = errorDao.getTransactionTimeOf(EntityType.LOCATION);
        LocationDeleteEntity locationDeleteEntity = LocationDeleteEntity.create(locationForDeletion.version(),
                PreservedId.create(locationForDeletion.id(), currentTransactionTime));
        long dataId = errorDao.insert(locationDeleteEntity);

        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.DELETE_LOCATION, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordLocationEditError(SubsystemException exception, LocationForEditing locationForEditing) {
        ExceptionData exceptionData = new ExceptionInserter().visit(exception, null);

        Instant currentTransactionTime = errorDao.getTransactionTimeOf(EntityType.LOCATION);
        LocationEditEntity locationEditEntity = LocationEditEntity.create(
                locationForEditing.version(),
                PreservedId.create(locationForEditing.id(), currentTransactionTime),
                clock.get(),
                locationForEditing.name(),
                locationForEditing.description());
        long dataId = errorDao.insert(locationEditEntity);

        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.EDIT_LOCATION, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordUnitAddError(SubsystemException exception, UnitAddForm input) {
        ExceptionData exceptionData = new ExceptionInserter().visit(exception, null);
        long dataId = errorDao.insert(UnitAddEntity.create(input.name(), input.abbreviation()));
        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.ADD_UNIT, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordUnitDeleteError(SubsystemException exception, Versionable<Unit> unit) {
        ExceptionData exceptionData = new ExceptionInserter().visit(exception, null);

        Instant currentTransactionTime = errorDao.getTransactionTimeOf(EntityType.UNIT);
        UnitDeleteEntity entity = UnitDeleteEntity.create(unit.version(), PreservedId.create(unit.id(), currentTransactionTime));
        long dataId = errorDao.insert(entity);

        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.DELETE_UNIT, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordUnitEditError(SubsystemException exception, UnitForEditing unit) {
        ExceptionData exceptionData = new ExceptionInserter().visit(exception, null);
        Instant currentTransactionTime = errorDao.getTransactionTimeOf(EntityType.UNIT);
        UnitEditEntity entity = UnitEditEntity.create(
                unit.version(),
                PreservedId.create(unit.id(), currentTransactionTime),
                clock.get(),
                unit.name(),
                unit.abbreviation());
        long dataId = errorDao.insert(entity);
        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.EDIT_UNIT, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordScaledUnitAddError(SubsystemException e, ScaledUnitAddForm form) {
        ExceptionData exceptionData = new ExceptionInserter().visit(e, null);
        Instant unitTransactionTime = errorDao.getTransactionTimeOf(EntityType.UNIT);
        ScaledUnitAddEntity entity = ScaledUnitAddEntity.create(form.scale(), PreservedId.create(form.unit(), unitTransactionTime));
        long dataId = errorDao.insert(entity);
        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.ADD_SCALED_UNIT, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordScaledUnitEditError(SubsystemException e, ScaledUnitForEditing scaledUnitForEditing) {
        ExceptionData exceptionData = new ExceptionInserter().visit(e, null);
        Instant currentTransactionTime = errorDao.getTransactionTimeOf(EntityType.SCALED_UNIT);
        Instant currentUnitTransactionTime = errorDao.getTransactionTimeOf(EntityType.UNIT);
        ScaledUnitEditEntity entity = ScaledUnitEditEntity.create(
                scaledUnitForEditing.version(),
                PreservedId.create(scaledUnitForEditing.id(), currentTransactionTime),
                clock.get(),
                scaledUnitForEditing.scale(),
                PreservedId.create(scaledUnitForEditing.unit(), currentUnitTransactionTime));
        long dataId = errorDao.insert(entity);
        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.EDIT_SCALED_UNIT, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordScaledUnitDeleteError(SubsystemException exception, Versionable<ScaledUnit> scaledUnitForDeletion) {
        ExceptionData exceptionData = new ExceptionInserter().visit(exception, null);
        Instant currentTransactionTime = errorDao.getTransactionTimeOf(EntityType.SCALED_UNIT);
        ScaledUnitDeleteEntity entity = ScaledUnitDeleteEntity.create(scaledUnitForDeletion.version(), PreservedId.create(scaledUnitForDeletion.id(), currentTransactionTime));
        long dataId = errorDao.insert(entity);
        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.DELETE_SCALED_UNIT, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordFoodAddError(SubsystemException exception, FoodAddForm input) {
        ExceptionData exceptionData = new ExceptionInserter().visit(exception, null);
        Instant scaledUnitTransactionTime = errorDao.getTransactionTimeOf(EntityType.SCALED_UNIT);
        Instant locationTransactionTime = errorDao.getTransactionTimeOf(EntityType.LOCATION);
        FoodAddEntity entity = FoodAddEntity.create(input.name(),
                input.toBuy(),
                input.expirationOffset(),
                NullablePreservedId.create(input.location().orElse(null), locationTransactionTime),
                PreservedId.create(input.storeUnit(), scaledUnitTransactionTime),
                input.description());
        long dataId = errorDao.insert(entity);
        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.ADD_FOOD, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordFoodDeleteError(SubsystemException exception, Versionable<Food> input) {
        ExceptionData exceptionData = new ExceptionInserter().visit(exception, null);
        Instant currentTransactionTime = errorDao.getTransactionTimeOf(EntityType.FOOD);
        FoodDeleteEntity entity = FoodDeleteEntity.create(input.version(), PreservedId.create(input.id(), currentTransactionTime));
        long dataId = errorDao.insert(entity);
        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.DELETE_FOOD, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordFoodEditError(SubsystemException e, FoodForEditing foodForEditing) {
        ExceptionData exceptionData = new ExceptionInserter().visit(e, null);
        Instant currentTransactionTime = errorDao.getTransactionTimeOf(EntityType.FOOD);
        Instant currentLocationTransactionTime = errorDao.getTransactionTimeOf(EntityType.LOCATION);
        Instant currentStoreUnitTransactionTime = errorDao.getTransactionTimeOf(EntityType.SCALED_UNIT);
        FoodEditEntity entity = FoodEditEntity.create(
                foodForEditing.version(),
                PreservedId.create(foodForEditing.id(), currentTransactionTime),
                foodForEditing.name(),
                foodForEditing.expirationOffset(),
                NullablePreservedId.create(foodForEditing.location().orElse(null), currentLocationTransactionTime),
                PreservedId.create(foodForEditing.storeUnit(), currentStoreUnitTransactionTime),
                foodForEditing.description(),
                clock.get());
        long dataId = errorDao.insert(entity);
        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.EDIT_FOOD, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordFoodItemAddError(SubsystemException e, FoodItemAddFormForErrorRecording item) {
        ExceptionData exceptionData = new ExceptionInserter().visit(e, null);
        Instant foodTransactionTime = errorDao.getTransactionTimeOf(EntityType.FOOD);
        Instant scaledUnitTransactionTime = errorDao.getTransactionTimeOf(EntityType.SCALED_UNIT);
        Instant locationTransactionTime = errorDao.getTransactionTimeOf(EntityType.LOCATION);
        FoodItemAddEntity entity = FoodItemAddEntity.create(
                item.eatBy(),
                PreservedId.create(item.ofType(), foodTransactionTime),
                PreservedId.create(item.storedIn(), locationTransactionTime),
                PreservedId.create(item.unit(), scaledUnitTransactionTime)
        );
        long dataId = errorDao.insert(entity);
        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.ADD_FOOD_ITEM, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordFoodItemDeleteError(SubsystemException exception, Versionable<FoodItem> input) {
        ExceptionData exceptionData = new ExceptionInserter().visit(exception, null);
        Instant currentTransactionTime = errorDao.getTransactionTimeOf(EntityType.FOOD_ITEM);
        FoodItemDeleteEntity entity = FoodItemDeleteEntity.create(input.version(), PreservedId.create(input.id(), currentTransactionTime));
        long dataId = errorDao.insert(entity);
        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.DELETE_FOOD_ITEM, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @Override
    public void recordFoodItemEditError(SubsystemException e, FoodItemForEditing item) {
        ExceptionData exceptionData = new ExceptionInserter().visit(e, null);
        Instant currentTransactionTime = errorDao.getTransactionTimeOf(EntityType.FOOD_ITEM);
        Instant currentLocationTransactionTime = errorDao.getTransactionTimeOf(EntityType.LOCATION);
        Instant currentScaledUnitTransactionTime = errorDao.getTransactionTimeOf(EntityType.SCALED_UNIT);
        FoodItemEditEntity entity = FoodItemEditEntity.create(
                PreservedId.create(item.id(), currentTransactionTime),
                item.eatBy(),
                PreservedId.create(item.storedIn(), currentLocationTransactionTime),
                PreservedId.create(item.unit(), currentScaledUnitTransactionTime),
                clock.get());
        long dataId = errorDao.insert(entity);
        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.EDIT_FOOD_ITEM, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
    }

    @AutoValue
    abstract static class ExceptionData {

        static ExceptionData create(ErrorEntity.ExceptionType exceptionType, long exceptionId) {
            return new AutoValue_ErrorRecorderImpl_ExceptionData(exceptionType, exceptionId);
        }

        abstract ErrorEntity.ExceptionType exceptionType();

        abstract long exceptionId();
    }

    private final class ExceptionInserter implements SubsystemException.Visitor<Void, ExceptionData> {

        @Override
        public ExceptionData subsystemException(SubsystemException exception, Void input) {
            String stacktrace = getStackTrace(exception);
            String message = getMessage(exception);
            SubsystemExceptionEntity error = SubsystemExceptionEntity.create(stacktrace, message);
            long id = errorDao.insert(error);
            return ExceptionData.create(ErrorEntity.ExceptionType.SUBSYSTEM_EXCEPTION, id);
        }

        @Override
        public ExceptionData statusCodeException(StatusCodeException exception, Void input) {
            String stacktrace = getStackTrace(exception);
            String message = getMessage(exception);
            StatusCodeExceptionEntity error = StatusCodeExceptionEntity.create(stacktrace, message, exception.getStatusCode());
            long id = errorDao.insert(error);
            return ExceptionData.create(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, id);
        }

        private String getMessage(SubsystemException exception) {
            return ofNullable(exception.getMessage()).orElse("");
        }

        private String getStackTrace(SubsystemException exception) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter stacktraceWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(stacktraceWriter);
            return stringWriter.toString();
        }
    }
}
