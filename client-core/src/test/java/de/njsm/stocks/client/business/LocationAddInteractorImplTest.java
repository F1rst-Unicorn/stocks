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

import de.njsm.stocks.client.business.entities.Job;
import de.njsm.stocks.client.business.entities.LocationAddForm;
import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.execution.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LocationAddInteractorImplTest {

    private LocationAddInteractorImpl uut;

    private Scheduler scheduler;

    private ErrorRecorder errorRecorder;

    private LocationAddService locationAddService;

    private Synchroniser synchroniser;

    @BeforeEach
    void setUp() {
        scheduler = mock(Scheduler.class);
        errorRecorder = mock(ErrorRecorder.class);
        locationAddService = mock(LocationAddService.class);
        synchroniser = mock(Synchroniser.class);
        uut = new LocationAddInteractorImpl(scheduler, errorRecorder, locationAddService, synchroniser);
    }

    @Test
    void addingLocationFromInterfaceQueuesTask() {
        uut.addLocation(getInput());

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.ADD_LOCATION, captor.getValue().name());
    }

    @Test
    void addingALocationFromFailingNetworkRecordsError() {
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        doThrow(exception).when(locationAddService).add(getInput());

        uut.addLocationInBackground(getInput());

        verify(locationAddService).add(getInput());
        verify(errorRecorder).recordLocationAddError(exception, getInput());
        verifyNoInteractions(synchroniser);
    }

    @Test
    void addingLocationIsForwardedAndSynchronised() {
        uut.addLocationInBackground(getInput());

        verify(locationAddService).add(getInput());
        verify(synchroniser).synchronise();
    }

    private LocationAddForm getInput() {
        return LocationAddForm.create("Fridge", "my fridge");
    }
}
