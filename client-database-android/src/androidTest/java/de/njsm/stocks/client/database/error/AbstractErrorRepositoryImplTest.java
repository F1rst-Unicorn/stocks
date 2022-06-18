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
import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.ErrorDetails;
import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.database.DbTestCase;
import de.njsm.stocks.client.database.UpdateDbEntity;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.client.database.Util.test;
import static de.njsm.stocks.client.database.Util.testList;
import static org.junit.Assert.assertTrue;

public abstract class AbstractErrorRepositoryImplTest extends DbTestCase {

    ErrorRecorder errorRecorder;

    ErrorRepository uut;

    @Before
    public void setup() {
        errorRecorder = new ErrorRecorderImpl(stocksDatabase.errorDao());
        uut = new ErrorRepositoryImpl(stocksDatabase.errorDao());

        List<UpdateDbEntity> updates = Arrays.stream(EntityType.values())
                .map(v -> UpdateDbEntity.create(v, Instant.EPOCH))
                .collect(Collectors.toList());
        stocksDatabase.synchronisationDao().insert(updates);
    }

    abstract ErrorDetails recordError(StatusCodeException e);

    abstract List<?> getErrorDetails();

    @Test
    public void gettingErrorWorks() {
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        ErrorDetails data = recordError(exception);

        test(uut.getNumberOfErrors()).assertValue(1);
        testList(uut.getErrors()).assertValue(v -> v.get(0).statusCode() == statusCode);
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorDetails().equals(data));
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorMessage().equals(exception.getMessage()));
    }

    @Test
    public void deletingErrorWorks() {
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        recordError(new StatusCodeException(statusCode));
        ErrorDescription input = testList(uut.getErrors()).values().get(0).get(0);

        uut.deleteError(input);

        assertTrue(stocksDatabase.errorDao().getStatusCodeErrors().isEmpty());
        test(uut.getNumberOfErrors()).assertValuesOnly(0);
        assertTrue(getErrorDetails().isEmpty());
    }
}
