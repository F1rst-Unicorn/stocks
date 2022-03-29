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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.ErrorRecorder;
import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.SubsystemException;
import de.njsm.stocks.client.business.entities.LocationAddForm;
import de.njsm.stocks.client.business.entities.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

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
}
