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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
abstract class DeleterImplTest<E extends Entity<E>> {

    EntityDeleter<E> uut;

    @Mock
    Scheduler scheduler;

    @Mock
    EntityDeleteService<E> deleteService;

    @Mock
    EntityDeleteRepository<E> deleteRepository;

    @Mock
    ErrorRecorder errorRecorder;

    @Mock
    Synchroniser synchroniser;

    abstract Job.Type getJobType();

    abstract void verifyRecorder(SubsystemException exception, Versionable<E> outputToService);

    @Test
    void deletingQueuesTask() {
        Identifiable<E> input = () -> 42;

        uut.delete(input);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(getJobType(), captor.getValue().name());
    }

    @Test
    void deletingInBackgroundWorks() {
        int id = 42;
        Identifiable<E> input = () -> id;
        Versionable<E> outputToService = getNetworkData(id, 3);
        when(deleteRepository.getEntityForDeletion(input)).thenReturn(outputToService);

        act(input);

        verify(deleteRepository).getEntityForDeletion(input);
        verify(deleteService).delete(outputToService);
        verify(synchroniser).synchronise();
    }

    @Test
    void failingDeletionIsRecorded() {
        int id = 42;
        Identifiable<E> input = () -> id;
        Versionable<E> outputToService = getNetworkData(id, 3);
        when(deleteRepository.getEntityForDeletion(input)).thenReturn(outputToService);
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        doThrow(exception).when(deleteService).delete(outputToService);

        act(input);

        verify(deleteRepository).getEntityForDeletion(input);
        verify(deleteService).delete(outputToService);
        verify(synchroniser).synchroniseAfterError(exception);
        verifyRecorder(exception, outputToService);
    }

    @Test
    void failingDeletionWithSubsystemExceptionIsRecorded() {
        int id = 42;
        Identifiable<E> input = () -> id;
        Versionable<E> outputToService = getNetworkData(id, 3);
        when(deleteRepository.getEntityForDeletion(input)).thenReturn(outputToService);
        SubsystemException exception = new SubsystemException("test");
        doThrow(exception).when(deleteService).delete(outputToService);

        act(input);

        verify(deleteRepository).getEntityForDeletion(input);
        verify(deleteService).delete(outputToService);
        verify(synchroniser).synchroniseAfterError(exception);
        verifyRecorder(exception, outputToService);
    }

    private void act(Identifiable<E> input) {
        uut.delete(input);
        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        captor.getValue().runnable().run();
    }

    private Versionable<E> getNetworkData(int id, int version) {
        return new Versionable<E>() {
            @Override
            public int version() {
                return version;
            }

            @Override
            public int id() {
                return id;
            }
        };
    }
}
