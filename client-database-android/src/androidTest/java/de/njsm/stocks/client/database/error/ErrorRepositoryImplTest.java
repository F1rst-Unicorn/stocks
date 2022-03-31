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
import de.njsm.stocks.client.business.ErrorRepository;
import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.SubsystemException;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.LocationAddForm;
import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.business.entities.SynchronisationErrorDetails;
import de.njsm.stocks.client.database.DbTestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ErrorRepositoryImplTest extends DbTestCase {

    private ErrorRecorder errorRecorder;

    private ErrorRepository uut;

    @Before
    public void setup() {
        errorRecorder = new ErrorRecorderImpl(stocksDatabase.errorDao());
        uut = new ErrorRepositoryImpl(stocksDatabase.errorDao());
    }

    @Test
    public void noErrorsInitially() {
        uut.getErrors().test().awaitCount(1).assertValue(List::isEmpty);
        uut.getNumberOfErrors().test().awaitCount(1).assertValue(0);
    }

    @Test
    public void locationAddErrorIsLoaded() {
        LocationAddForm form = LocationAddForm.create("Fridge", "the cold one");
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        errorRecorder.recordLocationAddError(exception, form);

        uut.getNumberOfErrors().test().awaitCount(1).assertValue(1);
        uut.getErrors().test().awaitCount(1)
                .assertValue(v -> v.get(0).statusCode() == StatusCode.DATABASE_UNREACHABLE);
        uut.getErrors().test().awaitCount(1)
                .assertValue(v -> v.get(0).errorDetails().equals(form));
    }

    @Test
    public void locationAddErrorIsLoadedWithSubsystemException() {
        LocationAddForm form = LocationAddForm.create("Fridge", "the cold one");
        SubsystemException exception = new SubsystemException("test");
        errorRecorder.recordLocationAddError(exception, form);

        uut.getNumberOfErrors().test().awaitCount(1).assertValue(1);
        uut.getErrors().test().awaitCount(1)
                .assertValue(v -> v.get(0).statusCode() == StatusCode.GENERAL_ERROR);
        uut.getErrors().test().awaitCount(1)
                .assertValue(v -> v.get(0).errorDetails().equals(form));
        uut.getErrors().test().awaitCount(1)
                .assertValue(v -> v.get(0).errorMessage().equals(exception.getMessage()));
    }

    @Test
    public void synchronisationErrorIsRetrieved() {
        SubsystemException exception = new SubsystemException("test");
        errorRecorder.recordSynchronisationError(exception);

        uut.getNumberOfErrors().test().awaitCount(1).assertValue(1);
        uut.getErrors().test().awaitCount(1)
                .assertValue(v -> v.get(0).statusCode() == StatusCode.GENERAL_ERROR);
        uut.getErrors().test().awaitCount(1)
                .assertValue(v -> v.get(0).errorDetails() instanceof SynchronisationErrorDetails);
        uut.getErrors().test().awaitCount(1)
                .assertValue(v -> v.get(0).errorMessage().equals(exception.getMessage()));
    }

    @Test
    public void locationAddErrorCanBeDeleted() {
        LocationAddForm form = LocationAddForm.create("Fridge", "the cold one");
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        errorRecorder.recordLocationAddError(exception, form);
        ErrorDescription input = uut.getErrors().test().awaitCount(1).values().get(0).get(0);

        uut.deleteError(input);

        assertTrue(stocksDatabase.errorDao().getStatusCodeErrors().isEmpty());
        stocksDatabase.errorDao().getNumberOfErrors().test().awaitCount(1).assertValue(0);
        assertTrue(stocksDatabase.errorDao().getLocationAdds().isEmpty());
    }

    @Test
    public void synchronisationErrorCanBeDeleted() {
        SubsystemException exception = new SubsystemException("test");
        errorRecorder.recordSynchronisationError(exception);
        ErrorDescription input = uut.getErrors().test().awaitCount(1).values().get(0).get(0);

        uut.deleteError(input);

        assertTrue(stocksDatabase.errorDao().getSubsystemErrors().isEmpty());
        stocksDatabase.errorDao().getNumberOfErrors().test().awaitCount(1).assertValue(0);
    }
}
