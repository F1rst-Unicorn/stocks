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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static io.reactivex.rxjava3.core.Observable.just;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScaledUnitEditInteractorImplTest {

    private ScaledUnitEditInteractorImpl uut;

    @Mock
    private ScaledUnitEditRepository repository;

    @Mock
    private ScaledUnitEditService service;

    @Mock
    private Synchroniser synchroniser;

    @Mock
    private ErrorRecorder errorRecorder;

    @Mock
    private Scheduler scheduler;

    @Mock
    private AfterErrorSynchroniser afterErrorSynchroniser;

    @BeforeEach
    void setUp() {
        uut = new ScaledUnitEditInteractorImpl(repository, service, synchroniser, errorRecorder, scheduler, afterErrorSynchroniser);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(synchroniser);
    }

    @Test
    void gettingFormDataWorks() {
        ScaledUnitToEdit scaledUnit = getInput();
        when(repository.getScaledUnit(scaledUnit)).thenReturn(just(scaledUnit));
        List<UnitForSelection> units = singletonList(UnitForSelection.create(scaledUnit.unit(), "Gramm"));
        when(repository.getUnitsForSelection()).thenReturn(just(units));

        Observable<ScaledUnitEditingFormData> actual = uut.getFormData(scaledUnit);

        actual.test().awaitCount(1).assertNoErrors().assertValue(v ->
                v.id() == scaledUnit.id() &&
                v.scale().compareTo(scaledUnit.scale()) == 0 &&
                v.availableUnits().equals(units) &&
                v.currentUnitListPosition() == 0
        );
        verify(repository).getScaledUnit(scaledUnit);
        verify(repository).getUnitsForSelection();
    }

    @Test
    void editingScaledUnitFromInterfaceQueuesTask() {
        uut.edit(getInput());

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.EDIT_SCALED_UNIT, captor.getValue().name());
    }

    @Test
    void editingWithoutChangeDoesNothing() {
        ScaledUnitToEdit input = getInput();
        ScaledUnitForEditing scaledUnitForEditing = input.withVersion(2);
        when(repository.getScaledUnitForSending(input)).thenReturn(scaledUnitForEditing);

        uut.editInBackground(input);

        verifyNoInteractions(service);
        verify(repository).getScaledUnitForSending(input);
    }

    @Test
    void differentDataSendsToService() {
        ScaledUnitToEdit localData = getInput();
        ScaledUnitToEdit editedUnit = ScaledUnitToEdit.create(
                localData.id(),
                localData.scale().add(BigDecimal.ONE),
                localData.unit() + 1);
        ScaledUnitForEditing scaledUnitForEditing = getInput().withVersion(2);
        ScaledUnitForEditing dataToNetwork = editedUnit.withVersion(2);
        when(repository.getScaledUnitForSending(editedUnit)).thenReturn(scaledUnitForEditing);

        uut.editInBackground(editedUnit);

        verify(service).edit(dataToNetwork);
        verify(repository).getScaledUnitForSending(editedUnit);
        verify(synchroniser).synchronise();
    }

    @Test
    void differentScaleSendsToService() {
        ScaledUnitToEdit localData = getInput();
        ScaledUnitToEdit editedUnit = ScaledUnitToEdit.create(
                localData.id(),
                localData.scale().add(BigDecimal.ONE),
                localData.unit());
        ScaledUnitForEditing scaledUnitForEditing = getInput().withVersion(2);
        ScaledUnitForEditing dataToNetwork = editedUnit.withVersion(2);
        when(repository.getScaledUnitForSending(editedUnit)).thenReturn(scaledUnitForEditing);

        uut.editInBackground(editedUnit);

        verify(service).edit(dataToNetwork);
        verify(repository).getScaledUnitForSending(editedUnit);
        verify(synchroniser).synchronise();
    }

    @Test
    void differentUnitSendsToService() {
        ScaledUnitToEdit localData = getInput();
        ScaledUnitToEdit editedUnit = ScaledUnitToEdit.create(
                localData.id(),
                localData.scale(),
                localData.unit() + 1);
        ScaledUnitForEditing scaledUnitForEditing = getInput().withVersion(2);
        ScaledUnitForEditing dataToNetwork = editedUnit.withVersion(2);
        when(repository.getScaledUnitForSending(editedUnit)).thenReturn(scaledUnitForEditing);

        uut.editInBackground(editedUnit);

        verify(service).edit(dataToNetwork);
        verify(repository).getScaledUnitForSending(editedUnit);
        verify(synchroniser).synchronise();
    }

    @Test
    void failingEditingIsRecorded() {
        ScaledUnitToEdit editedUnit = ScaledUnitToEdit.create(1, BigDecimal.valueOf(3), 5);
        ScaledUnitForEditing scaledUnitForEditing = getInput().withVersion(2);
        ScaledUnitForEditing expected = editedUnit.withVersion(scaledUnitForEditing.version());
        when(repository.getScaledUnitForSending(editedUnit)).thenReturn(scaledUnitForEditing);
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        doThrow(exception).when(service).edit(expected);

        uut.editInBackground(editedUnit);

        verify(service).edit(expected);
        verify(repository).getScaledUnitForSending(editedUnit);
        verify(errorRecorder).recordScaledUnitEditError(exception, expected);
        verifyNoInteractions(synchroniser);
    }

    private ScaledUnitToEdit getInput() {
        return ScaledUnitToEdit.create(1, BigDecimal.valueOf(3), 4);
    }
}