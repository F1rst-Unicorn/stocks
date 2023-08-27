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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.execution.Scheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static de.njsm.stocks.client.execution.SchedulerImplTest.runJobOnMocked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
abstract class DeleteInteractorImplTest<E> {

    EntityDeleteInteractor<E> uut;

    @Mock
    Scheduler scheduler;

    @Mock
    NewEntityDeleteService<E> deleteService;

    @Mock
    ErrorRecorder errorRecorder;

    @Mock
    Synchroniser synchroniser;

    abstract Job.Type getJobType();

    abstract E getNetworkData(int id, int version);

    abstract void verifyRecorder(SubsystemException exception, E outputToService);

    @Test
    void deletingQueuesTask() {
        E input = getNetworkData(42, 1);

        uut.delete(input);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(getJobType(), captor.getValue().name());
    }

    @Test
    void deletingInBackgroundWorks() {
        int id = 42;
        E input = getNetworkData(id, 1);

        act(input);

        verify(deleteService).delete(input);
        verify(synchroniser).synchronise();
    }

    @Test
    void failingDeletionIsRecorded() {
        int id = 42;
        E input = getNetworkData(id, 1);
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        doThrow(exception).when(deleteService).delete(input);

        act(input);

        verify(deleteService).delete(input);
        verify(synchroniser).synchroniseAfterError(exception);
        verifyRecorder(exception, input);
    }

    @Test
    void failingDeletionWithSubsystemExceptionIsRecorded() {
        int id = 42;
        E input = getNetworkData(id, 1);
        SubsystemException exception = new SubsystemException("test");
        doThrow(exception).when(deleteService).delete(input);

        act(input);

        verify(deleteService).delete(input);
        verify(synchroniser).synchroniseAfterError(exception);
        verifyRecorder(exception, input);
    }

    private void act(E input) {
        uut.delete(input);
        runJobOnMocked(scheduler);
    }
}
