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
import de.njsm.stocks.client.business.entities.Unit;
import de.njsm.stocks.client.business.entities.Versionable;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.verify;

class UnitDeleterImplTest extends DeleterImplTest<Unit> {

    @BeforeEach
    void setUp() {
        uut = new UnitDeleterImpl(deleteService, deleteRepository, synchroniser, errorRecorder, scheduler, afterErrorSynchroniser);
    }

    @Override
    Job.Type getJobType() {
        return Job.Type.DELETE_UNIT;
    }

    @Override
    void verifyRecorder(SubsystemException exception, Versionable<Unit> outputToService) {
        verify(errorRecorder).recordUnitDeleteError(exception, outputToService);
    }
}
