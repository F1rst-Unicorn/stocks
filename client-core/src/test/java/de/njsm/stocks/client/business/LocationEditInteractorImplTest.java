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
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.reactivex.rxjava3.core.Observable.just;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationEditInteractorImplTest {

    private LocationEditInteractorImpl uut;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationEditService locationEditService;

    @Mock
    private Synchroniser synchroniser;

    @Mock
    private Scheduler scheduler;

    @Mock
    private ErrorRecorder errorRecorder;

    @BeforeEach
    void setUp() {
        uut = new LocationEditInteractorImpl(locationRepository, locationEditService, synchroniser, scheduler, errorRecorder);
    }

    @Test
    void gettingLocationIsForwarded() {
        int id = 42;
        IdImpl<Location> input = IdImpl.create(id);
        LocationEditFormData expected = getLocationToEdit(id);
        when(locationRepository.getLocationForEditing(input)).thenReturn(just(expected));

        Observable<LocationEditFormData> actual = uut.getLocation(input);

        actual.test().assertValue(expected);
    }

    @Test
    void editingDispatchesToBackend() {
        LocationEditFormData expected = getLocationToEdit(42);

        uut.edit(expected.into());

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.EDIT_LOCATION, captor.getValue().name());
    }

    @Test
    void editingWithoutChangeDoesNothing() {
        int id = 42;
        LocationEditFormData sameData = getLocationToEdit(id);
        when(locationRepository.getLocationForEditing(sameData.id())).thenReturn(just(sameData));

        uut.editInBackground(sameData.into());

        verifyNoInteractions(locationEditService);
    }

    @Test
    void differentInputDataCausesEditing() {
        int id = 42;
        LocationEditFormData localData = getLocationToEdit(id);
        LocationEditFormData editedForm = LocationEditFormData.create(
                IdImpl.create(id),
                3,
                "edited name",
                "edited description");
        when(locationRepository.getLocationForEditing(editedForm.id())).thenReturn(just(localData));

        uut.editInBackground(editedForm.into());

        verify(locationEditService).editLocation(editedForm.into());
        verify(synchroniser).synchronise();
    }

    @Test
    void differentDescriptionCausesEditing() {
        int id = 42;
        LocationEditFormData localData = getLocationToEdit(id);
        LocationEditFormData editedForm = LocationEditFormData.create(
                IdImpl.create(id),
                3,
                "current name",
                "edited description");
        when(locationRepository.getLocationForEditing(editedForm.id())).thenReturn(just(localData));

        uut.editInBackground(editedForm.into());

        verify(locationEditService).editLocation(editedForm.into());
        verify(synchroniser).synchronise();
    }

    @Test
    void failingEditingErrorIsRecorded() {
        int id = 42;
        LocationEditFormData localData = getLocationToEdit(id);
        LocationEditFormData editedForm = LocationEditFormData.create(
                IdImpl.create(id),
                3,
                "current name",
                "edited description");
        SubsystemException exception = new SubsystemException("test");
        when(locationRepository.getLocationForEditing(editedForm.id())).thenReturn(just(localData));
        doThrow(exception).when(locationEditService).editLocation(editedForm.into());

        uut.editInBackground(editedForm.into());

        verify(locationEditService).editLocation(editedForm.into());
        verify(errorRecorder).recordLocationEditError(exception, editedForm.into());
        verify(synchroniser).synchroniseAfterError(exception);
    }

    private LocationEditFormData getLocationToEdit(int id) {
        return LocationEditFormData.create(
                IdImpl.create(id),
                3,
                "current name",
                "current description");
    }
}
