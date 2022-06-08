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
        Identifiable<Location> input = () -> id;
        LocationToEdit expected = getLocationToEdit(id);
        when(locationRepository.getLocationForEditing(input)).thenReturn(Observable.just(expected));

        Observable<LocationToEdit> actual = uut.getLocation(input);

        actual.test().assertValue(expected);
    }

    @Test
    void editingDispatchesToBackend() {
        LocationToEdit expected = getLocationToEdit(42);

        uut.edit(expected);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.EDIT_LOCATION, captor.getValue().name());
    }

    @Test
    void editingWithoutChangeDoesNothing() {
        int id = 42;
        LocationToEdit sameData = getLocationToEdit(id);
        when(locationRepository.getCurrentLocationBeforeEditing(sameData)).thenReturn(sameData.addVersion(2));

        uut.editInBackground(sameData);

        verifyNoInteractions(locationEditService);
    }

    @Test
    void differentInputDataCausesEditing() {
        int id = 42;
        int version = 2;
        LocationToEdit localData = getLocationToEdit(id);
        LocationToEdit editedForm = LocationToEdit.builder()
                .id(id)
                .name("edited name")
                .description("edited description")
                .build();
        LocationForEditing dataToNetwork = editedForm.addVersion(version);
        when(locationRepository.getCurrentLocationBeforeEditing(editedForm)).thenReturn(localData.addVersion(version));

        uut.editInBackground(editedForm);

        verify(locationEditService).editLocation(dataToNetwork);
        verify(synchroniser).synchronise();
    }

    @Test
    void differentDescriptionCausesEditing() {
        int id = 42;
        int version = 2;
        LocationToEdit localData = getLocationToEdit(id);
        LocationToEdit editedForm = LocationToEdit.builder()
                .id(id)
                .name(localData.name())
                .description("edited description")
                .build();
        LocationForEditing dataToNetwork = editedForm.addVersion(version);
        when(locationRepository.getCurrentLocationBeforeEditing(editedForm)).thenReturn(localData.addVersion(version));

        uut.editInBackground(editedForm);

        verify(locationEditService).editLocation(dataToNetwork);
        verify(synchroniser).synchronise();
    }

    @Test
    void failingEditingErrorIsRecorded() {
        int id = 42;
        int version = 2;
        LocationToEdit localData = getLocationToEdit(id);
        LocationToEdit editedForm = LocationToEdit.builder()
                .id(id)
                .name(localData.name())
                .description("edited description")
                .build();
        LocationForEditing dataToNetwork = editedForm.addVersion(version);
        when(locationRepository.getCurrentLocationBeforeEditing(editedForm)).thenReturn(localData.addVersion(version));
        SubsystemException exception = new SubsystemException("test");
        doThrow(exception).when(locationEditService).editLocation(dataToNetwork);

        uut.editInBackground(editedForm);

        verify(locationEditService).editLocation(dataToNetwork);
        verify(errorRecorder).recordLocationEditError(exception, dataToNetwork);
        verify(synchroniser).synchroniseAfterError(exception);
    }

    private LocationToEdit getLocationToEdit(int id) {
        return LocationToEdit.builder()
                .id(id)
                .name("current name")
                .description("current description")
                .build();
    }
}
