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
import de.njsm.stocks.client.business.entities.StatusCode;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ErrorRecorderImplTest extends DbTestCase {

    @Test
    public void recordingStatusCodeExceptionWorks() {
        ErrorRecorderImpl uut = new ErrorRecorderImpl(stocksDatabase.errorDao());
        StatusCodeException input = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        ErrorRecorder.Action expectedAction = ErrorRecorder.Action.SYNCHRONISATION;

        uut.recordError(expectedAction, input);

        assertEquals(1, stocksDatabase.errorDao().getStatusCodeErrors().size());
        StatusCodeExceptionEntity actual = stocksDatabase.errorDao().getStatusCodeErrors().get(0);
        assertEquals(input.getStatusCode(), actual.getStatusCode());
        assertEquals(expectedAction, actual.getAction());
        assertNotEquals(0, actual.getId());
    }

    @Test
    public void recordingSubsystemExceptionWorks() {
        ErrorRecorderImpl uut = new ErrorRecorderImpl(stocksDatabase.errorDao());
        SubsystemException input = new SubsystemException("message");
        ErrorRecorder.Action expectedAction = ErrorRecorder.Action.SYNCHRONISATION;

        uut.recordError(expectedAction, input);

        List<SubsystemExceptionEntity> recordedErrors = stocksDatabase.errorDao().getSubsystemErrors();
        assertEquals(1, recordedErrors.size());
        SubsystemExceptionEntity actual = recordedErrors.get(0);
        assertEquals(input.getMessage(), actual.getMessage());
        assertEquals(expectedAction, actual.getAction());
        assertNotEquals(0, actual.getId());
    }
}
