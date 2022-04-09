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

public class ErrorRetryInteractorImplTest {

    private ErrorRetryInteractorImpl uut;

    private LocationAddInteractor locationAddInteractor;

    private LocationDeleter locationDeleter;

    private Synchroniser synchroniser;

    private ErrorRepository errorRepository;

    private Scheduler scheduler;

    @BeforeEach
    void setUp() {
        locationAddInteractor = mock(LocationAddInteractor.class);
        locationDeleter = mock(LocationDeleter.class);
        synchroniser = mock(Synchroniser.class);
        errorRepository = mock(ErrorRepository.class);
        scheduler = mock(Scheduler.class);
        uut = new ErrorRetryInteractorImpl(locationAddInteractor, locationDeleter, synchroniser, scheduler, errorRepository);
    }

    @Test
    void retryingQueuesJob() {
        LocationAddForm locationAddForm = LocationAddForm.create("Fridge", "the cold one");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", locationAddForm);

        uut.retry(input);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.ADD_LOCATION, captor.getValue().name());
    }

    @Test
    void deletingErrorQueuesTask() {
        LocationAddForm locationAddForm = LocationAddForm.create("Fridge", "the cold one");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", locationAddForm);

        uut.delete(input);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.DELETE_ERROR, captor.getValue().name());
    }

    @Test
    void deletingAnErrorInBackgroundForwardsToRepository() {
        LocationAddForm locationAddForm = LocationAddForm.create("Fridge", "the cold one");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", locationAddForm);

        uut.deleteInBackground(input);

        verify(errorRepository).deleteError(input);
        verifyNoInteractions(locationAddInteractor);
        verifyNoInteractions(synchroniser);
    }

    @Test
    void retryingToAddLocationDispatchesToLocationAdder() {
        LocationAddForm locationAddForm = LocationAddForm.create("Fridge", "the cold one");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", locationAddForm);

        uut.retryInBackground(input);

        verify(locationAddInteractor).addLocation(locationAddForm);
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingSynchronisationDispatches() {
        SynchronisationErrorDetails synchronisationErrorDetails = SynchronisationErrorDetails.create();
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", synchronisationErrorDetails);

        uut.retryInBackground(input);

        verify(synchroniser).synchronise();
        verify(errorRepository).deleteError(input);
    }
}
