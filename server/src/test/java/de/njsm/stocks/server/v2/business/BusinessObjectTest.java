package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.db.FailSafeDatabaseHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class BusinessObjectTest {

    private BusinessObject uut;

    private FailSafeDatabaseHandler backend;

    @Before
    public void setup() {
        backend = Mockito.mock(FailSafeDatabaseHandler.class);
        uut = new BusinessObject();
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(backend);
    }

    @Test
    public void committingWorks() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.finishTransaction(StatusCode.SUCCESS, backend);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).commit();
    }

    @Test
    public void failingCommitIsPropagated() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.finishTransaction(StatusCode.SUCCESS, backend);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        Mockito.verify(backend).commit();
    }

    @Test
    public void rollingBackWorks() {
        Mockito.when(backend.rollback()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.finishTransaction(StatusCode.DATABASE_UNREACHABLE, backend);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        Mockito.verify(backend).rollback();
    }

    @Test
    public void committingWithResultWorks() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.finishTransaction(Validation.success("yei"), backend);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).commit();
    }

    @Test
    public void failingCommitWithResultIsPropagated() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        Validation<StatusCode, String> result = uut.finishTransaction(Validation.success("yei"), backend);

        assertTrue(result.isFail());
        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());
        Mockito.verify(backend).commit();
    }

    @Test
    public void rollingBackWithResultWorks() {
        Mockito.when(backend.rollback()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.finishTransaction(Validation.fail(StatusCode.DATABASE_UNREACHABLE), backend);

        assertTrue(result.isFail());
        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());
        Mockito.verify(backend).rollback();
    }

}