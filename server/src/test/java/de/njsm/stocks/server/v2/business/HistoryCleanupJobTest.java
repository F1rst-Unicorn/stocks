package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.db.CrudDatabaseHandler;
import de.njsm.stocks.server.v2.db.TransactionHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Period;
import java.util.Collections;

public class HistoryCleanupJobTest {

    private HistoryCleanupJob uut;

    private Period maxHistory;

    private TransactionHandler transactionHandler;

    private CrudDatabaseHandler<?, ?> tableHandler;

    @Before
    public void setup() {
        maxHistory = Period.ofDays(1);
        transactionHandler = Mockito.mock(TransactionHandler.class);
        tableHandler = Mockito.mock(CrudDatabaseHandler.class);
        uut = new HistoryCleanupJob(maxHistory, transactionHandler, Collections.singletonList(tableHandler));
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(transactionHandler);
        Mockito.verifyNoMoreInteractions(tableHandler);
    }

    @Test
    public void testSuccessfulCleanup() {
        Mockito.when(tableHandler.cleanDataOlderThan(maxHistory)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(transactionHandler.commit()).thenReturn(StatusCode.SUCCESS);

        uut.run();

        Mockito.verify(tableHandler).cleanDataOlderThan(maxHistory);
        Mockito.verify(transactionHandler).commit();
        Mockito.verify(transactionHandler).commit();
    }

    @Test
    public void testFailingCleanup() {
        Mockito.when(tableHandler.cleanDataOlderThan(maxHistory)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        Mockito.when(transactionHandler.rollback()).thenReturn(StatusCode.SUCCESS);

        uut.run();

        Mockito.verify(tableHandler).cleanDataOlderThan(maxHistory);
        Mockito.verify(transactionHandler).rollback();
    }

    @Test
    public void testFailingCleanupWithFailingRollback() {
        Mockito.when(tableHandler.cleanDataOlderThan(maxHistory)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        Mockito.when(transactionHandler.rollback()).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        uut.run();

        Mockito.verify(tableHandler).cleanDataOlderThan(maxHistory);
        Mockito.verify(transactionHandler).rollback();
    }

    @Test
    public void testFailingCommit() {
        Mockito.when(tableHandler.cleanDataOlderThan(maxHistory)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(transactionHandler.commit()).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        uut.run();

        Mockito.verify(tableHandler).cleanDataOlderThan(maxHistory);
        Mockito.verify(transactionHandler).commit();
    }
}
