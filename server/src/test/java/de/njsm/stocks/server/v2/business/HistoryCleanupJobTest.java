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

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.db.CrudDatabaseHandler;
import de.njsm.stocks.server.v2.db.PrincipalsHandler;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Period;
import java.util.List;

import static de.njsm.stocks.server.v2.business.CaConsistencyCheckJobTest.TEST_USER;
import static org.mockito.Mockito.*;

public class HistoryCleanupJobTest {

    private HistoryCleanupJob uut;

    private Period maxHistory;

    private PrincipalsHandler principalsHandler;

    private CrudDatabaseHandler<?, ?> tableHandler;

    private CrudDatabaseHandler<?, ?> tableHandler2;

    @BeforeEach
    public void setup() {
        maxHistory = Period.ofDays(1);
        principalsHandler = mock(PrincipalsHandler.class);
        tableHandler = mock(CrudDatabaseHandler.class);
        tableHandler2 = mock(CrudDatabaseHandler.class);
        uut = new HistoryCleanupJob(maxHistory, principalsHandler, List.of(tableHandler, tableHandler2));
    }

    @AfterEach
    public void tearDown() {
        verify(tableHandler).setPrincipals(TEST_USER);
        verify(tableHandler2).setPrincipals(TEST_USER);
        verify(principalsHandler).getJobRunnerPrincipal();
        verifyNoMoreInteractions(principalsHandler);
        verifyNoMoreInteractions(tableHandler);
        verifyNoMoreInteractions(tableHandler2);
    }

    @Test
    public void testSuccessfulCleanup() {
        when(principalsHandler.getJobRunnerPrincipal()).thenReturn(Validation.success(TEST_USER));
        when(tableHandler.cleanDataOlderThan(maxHistory)).thenReturn(StatusCode.SUCCESS);
        when(tableHandler2.cleanDataOlderThan(maxHistory)).thenReturn(StatusCode.SUCCESS);
        when(principalsHandler.commit()).thenReturn(StatusCode.SUCCESS);

        uut.runJob();

        verify(tableHandler).cleanDataOlderThan(maxHistory);
        verify(tableHandler2).cleanDataOlderThan(maxHistory);
        verify(principalsHandler).commit();
        verify(principalsHandler).commit();
    }

    @Test
    public void testFailingCleanup() {
        when(principalsHandler.getJobRunnerPrincipal()).thenReturn(Validation.success(TEST_USER));
        when(tableHandler.cleanDataOlderThan(maxHistory)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        when(principalsHandler.rollback()).thenReturn(StatusCode.SUCCESS);

        uut.runJob();

        verify(tableHandler).cleanDataOlderThan(maxHistory);
        verify(principalsHandler).rollback();
    }

    @Test
    public void testFailingCleanupInSecondHandler() {
        when(principalsHandler.getJobRunnerPrincipal()).thenReturn(Validation.success(TEST_USER));
        when(tableHandler.cleanDataOlderThan(maxHistory)).thenReturn(StatusCode.SUCCESS);
        when(tableHandler2.cleanDataOlderThan(maxHistory)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        when(principalsHandler.rollback()).thenReturn(StatusCode.SUCCESS);

        uut.runJob();

        verify(tableHandler).cleanDataOlderThan(maxHistory);
        verify(tableHandler2).cleanDataOlderThan(maxHistory);
        verify(principalsHandler).rollback();
    }
}
