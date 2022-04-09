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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class LocationDeleterImplTest {

    private LocationDeleterImpl uut;

    private Scheduler scheduler;

    private LocationDeleteService locationDeleteService;

    private LocationRepository locationRepository;

    private ErrorRecorder errorRecorder;

    private Synchroniser synchroniser;

    @BeforeEach
    void setUp() {
        scheduler = mock(Scheduler.class);
        locationDeleteService = mock(LocationDeleteService.class);
        locationRepository = mock(LocationRepository.class);
        errorRecorder = mock(ErrorRecorder.class);
        synchroniser = mock(Synchroniser.class);
        uut = new LocationDeleterImpl(locationDeleteService, locationRepository, synchroniser, errorRecorder, scheduler);
    }

    @Test
    void deletingQueuesTask() {
        Identifiable<Location> input = () -> 42;

        uut.deleteLocation(input);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.DELETE_LOCATION, captor.getValue().name());
    }

    @Test
    void deletingInBackgroundWorks() {
        int id = 42;
        Identifiable<Location> input = () -> id;
        LocationForDeletion outputToService = LocationForDeletion.builder()
                .id(id)
                .version(3)
                .build();
        when(locationRepository.getLocation(input)).thenReturn(outputToService);

        uut.deleteLocationInBackground(input);

        verify(locationRepository).getLocation(input);
        verify(locationDeleteService).deleteLocation(outputToService);
        verify(synchroniser).synchronise();
    }

    @Test
    void failingDeletionIsRecorded() {
        int id = 42;
        Identifiable<Location> input = () -> id;
        LocationForDeletion outputToService = LocationForDeletion.builder()
                .id(id)
                .version(3)
                .build();
        when(locationRepository.getLocation(input)).thenReturn(outputToService);
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        doThrow(exception).when(locationDeleteService).deleteLocation(outputToService);

        uut.deleteLocationInBackground(input);

        verify(locationRepository).getLocation(input);
        verify(locationDeleteService).deleteLocation(outputToService);
        verifyNoInteractions(synchroniser);
        verify(errorRecorder).recordLocationDeleteError(exception, outputToService);
    }

    @Test
    void failingDeletionWithSubsystemExceptionIsRecorded() {
        int id = 42;
        Identifiable<Location> input = () -> id;
        LocationForDeletion outputToService = LocationForDeletion.builder()
                .id(id)
                .version(3)
                .build();
        when(locationRepository.getLocation(input)).thenReturn(outputToService);
        SubsystemException exception = new SubsystemException("test");
        doThrow(exception).when(locationDeleteService).deleteLocation(outputToService);

        uut.deleteLocationInBackground(input);

        verify(locationRepository).getLocation(input);
        verify(locationDeleteService).deleteLocation(outputToService);
        verifyNoInteractions(synchroniser);
        verify(errorRecorder).recordLocationDeleteError(exception, outputToService);
    }

    @Test
    void failingDeletionWithOutdatedDataTriggersSynchronisation() {
        int id = 42;
        Identifiable<Location> input = () -> id;
        LocationForDeletion outputToService = LocationForDeletion.builder()
                .id(id)
                .version(3)
                .build();
        when(locationRepository.getLocation(input)).thenReturn(outputToService);
        StatusCodeException exception = new StatusCodeException(StatusCode.INVALID_DATA_VERSION);
        doThrow(exception).when(locationDeleteService).deleteLocation(outputToService);

        uut.deleteLocationInBackground(input);

        verify(locationRepository).getLocation(input);
        verify(locationDeleteService).deleteLocation(outputToService);
        verify(synchroniser).synchronise();
        verify(errorRecorder).recordLocationDeleteError(exception, outputToService);
    }
}
