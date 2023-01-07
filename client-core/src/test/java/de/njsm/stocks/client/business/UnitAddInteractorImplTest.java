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
import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.business.entities.UnitAddForm;
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
class UnitAddInteractorImplTest {

    private UnitAddInteractorImpl uut;

    @Mock
    private UnitAddService unitAddService;

    @Mock
    private Synchroniser synchroniser;

    @Mock
    private ErrorRecorder errorRecorder;

    @Mock
    private Scheduler scheduler;

    @BeforeEach
    void setUp() {
        uut = new UnitAddInteractorImpl(unitAddService, synchroniser, errorRecorder, scheduler);
    }

    @Test
    void addingUnitFromInterfaceQueuesTask() {
        uut.addUnit(getInput());

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(Job.Type.ADD_UNIT, captor.getValue().name());
    }

    @Test
    void addingAUnitFromFailingNetworkRecordsError() {
        StatusCodeException exception = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        doThrow(exception).when(unitAddService).addUnit(getInput());

        uut.addUnitInBackground(getInput());

        verify(unitAddService).addUnit(getInput());
        verify(errorRecorder).recordUnitAddError(exception, getInput());
        verifyNoInteractions(synchroniser);
    }

    @Test
    void addingUnitIsForwardedAndSynchronised() {
        uut.addUnitInBackground(getInput());

        verify(unitAddService).addUnit(getInput());
        verify(synchroniser).synchronise();
    }

    private UnitAddForm getInput() {
        return UnitAddForm.create("Gramm", "g");
    }
}
