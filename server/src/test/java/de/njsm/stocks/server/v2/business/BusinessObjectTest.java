/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.db.FailSafeDatabaseHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.times;

public class BusinessObjectTest {

    private BusinessObject uut;

    private FailSafeDatabaseHandler backend;

    @Before
    public void setup() {
        backend = Mockito.mock(FailSafeDatabaseHandler.class);
        uut = new BusinessObject(backend);
        uut.setPrincipals(TEST_USER);
    }

    @After
    public void tearDown() {
        Mockito.verify(backend).setPrincipals(TEST_USER);
        Mockito.verifyNoMoreInteractions(backend);
    }

    @Test
    public void successfulTransactionIsCommitted() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);
        int[] integerBox = new int[1];

        StatusCode result = uut.runOperation(() -> {
            integerBox[0]++;
            return StatusCode.SUCCESS;
        });

        assertEquals(StatusCode.SUCCESS, result);
        assertEquals(1, integerBox[0]);
        Mockito.verify(backend).commit();
    }

    @Test
    public void asyncRequestIsUsedCorrectly() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);

        Validation<StatusCode, StatusCode> result = uut.runAsynchronously(r, () -> Validation.success(StatusCode.SUCCESS));

        assertEquals(StatusCode.SUCCESS, result.success());
        Mockito.verify(r).register(Mockito.any(CompletionCallback.class));
        Mockito.verifyNoMoreInteractions(r);
    }

    @Test
    public void failingTransactionIsRunOnce() {
        Mockito.when(backend.rollback()).thenReturn(StatusCode.SUCCESS);
        int[] integerBox = new int[1];

        StatusCode result = uut.runOperation(() -> {
            integerBox[0]++;
            return StatusCode.DATABASE_UNREACHABLE;
        });

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        assertEquals(1, integerBox[0]);
        Mockito.verify(backend).rollback();
    }

    @Test
    public void failureDuringCommitIsReported() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        int[] integerBox = new int[1];

        StatusCode result = uut.runOperation(() -> {
            integerBox[0]++;
            return StatusCode.SUCCESS;
        });

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        assertEquals(1, integerBox[0]);
        Mockito.verify(backend).commit();
    }

    @Test
    public void repeatUnserialisableFunction() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.SERIALISATION_CONFLICT, StatusCode.SUCCESS);
        int[] integerBox = new int[1];

        Validation<StatusCode, Object> result = uut.runFunction(() -> {
            integerBox[0]++;
            return Validation.success(null);
        });

        assertTrue(result.isSuccess());
        assertEquals(2, integerBox[0]);
        Mockito.verify(backend, times(2)).commit();
    }

    @Test
    public void repeatUnserialisableOperation() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.SERIALISATION_CONFLICT, StatusCode.SUCCESS);
        int[] integerBox = new int[1];

        StatusCode result = uut.runOperation(() -> {
            integerBox[0]++;
            return StatusCode.SUCCESS;
        });

        assertEquals(StatusCode.SUCCESS, result);
        assertEquals(2, integerBox[0]);
        Mockito.verify(backend, times(2)).commit();
    }

    @Test
    public void committingWorks() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, StatusCode> result = uut.finishTransaction(StatusCode.SUCCESS.toValidation());

        assertEquals(StatusCode.SUCCESS, result.success());
        Mockito.verify(backend).commit();
    }

    @Test
    public void failingCommitIsPropagated() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        Validation<StatusCode, StatusCode> result = uut.finishTransaction(StatusCode.SUCCESS.toValidation());

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());
        Mockito.verify(backend).commit();
    }

    @Test
    public void rollingBackWorks() {
        Mockito.when(backend.rollback()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, StatusCode> result = uut.finishTransaction(StatusCode.DATABASE_UNREACHABLE.toValidation());

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());
        Mockito.verify(backend).rollback();
    }

    @Test
    public void committingWithResultWorks() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.finishTransaction(Validation.success("yei"));

        assertTrue(result.isSuccess());
        Mockito.verify(backend).commit();
    }

    @Test
    public void failingCommitWithResultIsPropagated() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        Validation<StatusCode, String> result = uut.finishTransaction(Validation.success("yei"));

        assertTrue(result.isFail());
        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());
        Mockito.verify(backend).commit();
    }

    @Test
    public void rollingBackWithResultWorks() {
        Mockito.when(backend.rollback()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.finishTransaction(Validation.fail(StatusCode.DATABASE_UNREACHABLE));

        assertTrue(result.isFail());
        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());
        Mockito.verify(backend).rollback();
    }

    @Test
    public void noThrowableLeadsToCommit() {

        uut.finishTransaction((Throwable) null);

        Mockito.verify(backend).commit();
    }

    @Test
    public void throwableLeadsToRollback() {

        uut.finishTransaction(new Exception("test"));

        Mockito.verify(backend).rollback();
    }
}
