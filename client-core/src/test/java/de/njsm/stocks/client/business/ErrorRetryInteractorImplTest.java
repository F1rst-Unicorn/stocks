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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ErrorRetryInteractorImplTest {

    private ErrorRetryInteractorImpl uut;

    @Mock
    private LocationAddInteractor locationAddInteractor;

    @Mock
    private LocationDeleter locationDeleter;

    @Mock
    private LocationEditInteractor locationEditInteractor;

    @Mock
    private Synchroniser synchroniser;

    @Mock
    private ErrorRepository errorRepository;

    @Mock
    private Scheduler scheduler;

    @BeforeEach
    void setUp() {
        uut = new ErrorRetryInteractorImpl(locationAddInteractor, locationDeleter, locationEditInteractor, synchroniser, scheduler, errorRepository);
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

    @Test
    void retryingLocationDeletingDispatches() {
        LocationDeleteErrorDetails locationDeleteErrorDetails = LocationDeleteErrorDetails.create(1, "Fridge");
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", locationDeleteErrorDetails);

        uut.retryInBackground(input);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Identifiable<Location>> captor = ArgumentCaptor.forClass(Identifiable.class);
        verify(locationDeleter).deleteLocation(captor.capture());
        assertEquals(locationDeleteErrorDetails.id(), captor.getValue().id());
        verify(errorRepository).deleteError(input);
    }

    @Test
    void retryingLocationEditingDispatches() {
        LocationEditErrorDetails locationEditErrorDetails = LocationEditErrorDetails.create(1, "Fridge", "The cold one");
        LocationToEdit expected = LocationToEdit.builder()
                .id(locationEditErrorDetails.id())
                .name(locationEditErrorDetails.name())
                .description(locationEditErrorDetails.description())
                .build();
        ErrorDescription input = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "test", locationEditErrorDetails);

        uut.retryInBackground(input);

        verify(locationEditInteractor).edit(expected);
        verify(errorRepository).deleteError(input);
    }
}
