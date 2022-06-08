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
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ErrorRecorderImplTest extends DbTestCase {

    private ErrorRecorder uut;

    @Before
    public void setup() {
        uut = new ErrorRecorderImpl(stocksDatabase.errorDao());
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
        assertEquals(1, locationDeleteEntities.get(0).id());
        assertEquals(locationForDeletion.id(), locationDeleteEntities.get(0).locationId());
        assertEquals(locationForDeletion.version(), locationDeleteEntities.get(0).version());
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
        assertEquals(locationForEditing.id(), locationEditEntity.locationId());
        assertEquals(locationForEditing.version(), locationEditEntity.version());
        assertEquals(locationForEditing.name(), locationEditEntity.name());
        assertEquals(locationForEditing.description(), locationEditEntity.description());
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
        assertEquals(1, unitDeleteEntities.get(0).id());
        assertEquals(unitForDeletion.id(), unitDeleteEntities.get(0).unitId());
        assertEquals(unitForDeletion.version(), unitDeleteEntities.get(0).version());
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
        assertEquals(unitForEditing.id(), unitEditEntity.unitId());
        assertEquals(unitForEditing.version(), unitEditEntity.version());
        assertEquals(unitForEditing.name(), unitEditEntity.name());
        assertEquals(unitForEditing.abbreviation(), unitEditEntity.abbreviation());
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
        assertEquals(form.unit(), scaledUnitAdds.get(0).unit());
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
        assertEquals(form.id(), scaledUnitEditEntity.id());
        assertEquals(form.version(), scaledUnitEditEntity.version());
        assertEquals(form.scale(), scaledUnitEditEntity.scale());
        assertEquals(form.unit(), scaledUnitEditEntity.unit());
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
        assertEquals(form.id(), scaledUnitDeleteEntity.id());
        assertEquals(form.version(), scaledUnitDeleteEntity.version());
        List<ErrorEntity> errors = stocksDatabase.errorDao().getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorEntity.Action.DELETE_SCALED_UNIT, errors.get(0).action());
        assertEquals(1, errors.get(0).dataId());
        assertEquals(ErrorEntity.ExceptionType.STATUSCODE_EXCEPTION, errors.get(0).exceptionType());
        assertEquals(1, errors.get(0).exceptionId());
    }
}
