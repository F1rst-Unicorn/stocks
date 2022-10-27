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
class UnitEditInteractorImplTest {

    private UnitEditInteractorImpl uut;

    @Mock
    private UnitRepository repository;

    @Mock
    private UnitEditService editService;

    @Mock
    private Synchroniser synchroniser;

    @Mock
    private Scheduler scheduler;

    @Mock
    private ErrorRecorder errorRecorder;

    @BeforeEach
    void setUp() {
        uut = new UnitEditInteractorImpl(repository, editService, synchroniser, scheduler, errorRecorder);
    }

    @Test
    void gettingLocationIsForwarded() {
        int id = 42;
        Id<Unit> input = () -> id;
        UnitToEdit expected = getDataToEdit(id);
        when(repository.getUnit(input)).thenReturn(Observable.just(expected));

        Observable<UnitToEdit> actual = uut.get(input);

        actual.test().assertValue(expected);
    }

    @Test
    void editingDispatchesToBackend() {
        UnitToEdit expected = getDataToEdit(42);

        uut.edit(expected);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.EDIT_UNIT, captor.getValue().name());
    }

    @Test
    void editingWithoutChangeDoesNothing() {
        int id = 42;
        UnitToEdit sameData = getDataToEdit(id);
        when(repository.getCurrentDataBeforeEditing(sameData)).thenReturn(sameData.addVersion(2));

        uut.editInBackground(sameData);

        verifyNoInteractions(editService);
    }

    @Test
    void differentInputDataCausesEditing() {
        int id = 42;
        int version = 2;
        UnitToEdit localData = getDataToEdit(id);
        UnitToEdit editedForm = UnitToEdit.builder()
                .id(id)
                .name("edited name")
                .abbreviation("edited abbreviation")
                .build();
        UnitForEditing dataToNetwork = editedForm.addVersion(version);
        when(repository.getCurrentDataBeforeEditing(editedForm)).thenReturn(localData.addVersion(version));

        uut.editInBackground(editedForm);

        verify(editService).edit(dataToNetwork);
        verify(synchroniser).synchronise();
    }

    @Test
    void differentNameCausesEditing() {
        int id = 42;
        int version = 2;
        UnitToEdit localData = getDataToEdit(id);
        UnitToEdit editedForm = UnitToEdit.builder()
                .id(id)
                .name("edited name")
                .abbreviation(localData.name())
                .build();
        UnitForEditing dataToNetwork = editedForm.addVersion(version);
        when(repository.getCurrentDataBeforeEditing(editedForm)).thenReturn(localData.addVersion(version));

        uut.editInBackground(editedForm);

        verify(editService).edit(dataToNetwork);
        verify(synchroniser).synchronise();
    }

    @Test
    void differentDescriptionCausesEditing() {
        int id = 42;
        int version = 2;
        UnitToEdit localData = getDataToEdit(id);
        UnitToEdit editedForm = UnitToEdit.builder()
                .id(id)
                .name(localData.name())
                .abbreviation("edited abbreviation")
                .build();
        UnitForEditing dataToNetwork = editedForm.addVersion(version);
        when(repository.getCurrentDataBeforeEditing(editedForm)).thenReturn(localData.addVersion(version));

        uut.editInBackground(editedForm);

        verify(editService).edit(dataToNetwork);
        verify(synchroniser).synchronise();
    }

    @Test
    void failingEditingErrorIsRecorded() {
        int id = 42;
        int version = 2;
        UnitToEdit localData = getDataToEdit(id);
        UnitToEdit editedForm = UnitToEdit.builder()
                .id(id)
                .name(localData.name())
                .abbreviation("edited abbreviation")
                .build();
        UnitForEditing dataToNetwork = editedForm.addVersion(version);
        when(repository.getCurrentDataBeforeEditing(editedForm)).thenReturn(localData.addVersion(version));
        SubsystemException exception = new SubsystemException("test");
        doThrow(exception).when(editService).edit(dataToNetwork);

        uut.editInBackground(editedForm);

        verify(editService).edit(dataToNetwork);
        verify(errorRecorder).recordUnitEditError(exception, dataToNetwork);
        verify(synchroniser).synchroniseAfterError(exception);
    }

    private UnitToEdit getDataToEdit(int id) {
        return UnitToEdit.builder()
                .id(id)
                .name("current name")
                .abbreviation("current abbreviation")
                .build();
    }
}
