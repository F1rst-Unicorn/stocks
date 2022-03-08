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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.entities.Update;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SynchronisationRepositoryImplTest {

    private SynchronisationRepositoryImpl uut;

    private SynchronisationDao synchronisationDao;

    @Before
    public void setUp() {
        synchronisationDao = mock(SynchronisationDao.class);
        uut = new SynchronisationRepositoryImpl(synchronisationDao);
    }

    @Test
    public void updateListIsReturned() {
        when(synchronisationDao.getAll()).thenReturn(emptyList());

        List<Update> actual = uut.getUpdates();

        assertEquals(emptyList(), actual);
        verify(synchronisationDao).getAll();
    }

    @Test
    public void updatesToWriteAreForwarded() {
        uut.writeUpdates(emptyList());

        verify(synchronisationDao).writeUpdates(emptyList());
    }

    @Test
    public void locationsToWriteAreForwarded() {
        uut.writeLocations(emptyList());

        verify(synchronisationDao).writeLocations(emptyList());
    }

    @Test
    public void locationsToInitialiseAreForwarded() {
        uut.initialiseLocations(emptyList());

        verify(synchronisationDao).synchroniseLocations(emptyList());
    }
}
