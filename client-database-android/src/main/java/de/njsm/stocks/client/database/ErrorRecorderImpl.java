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

import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.StringWriter;

import static java.util.Optional.ofNullable;

class ErrorRecorderImpl implements ErrorRecorder {

    private final ErrorDao errorDao;

    @Inject
    ErrorRecorderImpl(ErrorDao errorDao) {
        this.errorDao = errorDao;
    }

    @Override
    public void recordError(Action action, SubsystemException input) {
        new Inserter().visit(input, action);
    }

    private final class Inserter implements SubsystemException.Visitor<Action, Void> {

        @Override
        public Void subsystemException(SubsystemException exception, Action input) {
            String stacktrace = getStackTrace(exception);
            String message = getMessage(exception);
            SubsystemExceptionEntity error = new SubsystemExceptionEntity(0, input, stacktrace, message);
            errorDao.insert(error);
            return null;
        }

        @Override
        public Void statusCodeException(StatusCodeException exception, Action input) {
            String stacktrace = getStackTrace(exception);
            String message = getMessage(exception);
            StatusCodeExceptionEntity error = new StatusCodeExceptionEntity(0, input, stacktrace, message, exception.getStatusCode());
            errorDao.insert(error);
            return null;
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
