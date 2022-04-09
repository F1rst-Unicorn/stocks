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
import de.njsm.stocks.client.business.ErrorRecorder;
import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.SubsystemException;
import de.njsm.stocks.client.business.entities.LocationAddForm;
import de.njsm.stocks.client.business.entities.LocationForDeletion;
import de.njsm.stocks.client.database.DataMapper;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.StringWriter;

import static de.njsm.stocks.client.database.DataMapper.map;
import static java.util.Optional.ofNullable;

public class ErrorRecorderImpl implements ErrorRecorder {

    private final ErrorDao errorDao;

    @Inject
    ErrorRecorderImpl(ErrorDao errorDao) {
        this.errorDao = errorDao;
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
    public void recordLocationDeleteError(SubsystemException exception, LocationForDeletion locationForDeletion) {
        ExceptionData exceptionData = new ExceptionInserter().visit(exception, null);

        LocationDeleteEntity locationDeleteEntity = DataMapper.map(locationForDeletion);
        long dataId = errorDao.insert(locationDeleteEntity);

        errorDao.insert(ErrorEntity.create(ErrorEntity.Action.DELETE_LOCATION, dataId, exceptionData.exceptionType(), exceptionData.exceptionId()));
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
