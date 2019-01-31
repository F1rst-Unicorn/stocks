package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.Update;
import de.njsm.stocks.server.v2.db.UpdateBackend;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class UpdateManagerTest {

    private UpdateManager uut;

    private UpdateBackend backend;

    @Before
    public void setup() {
        backend = Mockito.mock(UpdateBackend.class);
        uut = new UpdateManager(backend);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(backend);
    }

    @Test
    public void gettingUpdatesWorks() {
        Mockito.when(backend.getUpdates()).thenReturn(Validation.success(Collections.emptyList()));
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, List<Update>> result = uut.getUpdates();

        assertTrue(result.isSuccess());
        Mockito.verify(backend).getUpdates();
        Mockito.verify(backend).commit();
    }

}