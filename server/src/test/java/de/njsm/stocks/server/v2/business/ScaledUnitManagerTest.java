/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.impl.ScaledUnitForDeletion;
import de.njsm.stocks.common.api.impl.ScaledUnitForEditing;
import de.njsm.stocks.server.v2.db.ScaledUnitHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ScaledUnitManagerTest {

    private ScaledUnitManager uut;

    private ScaledUnitHandler dbHandler;

    @Before
    public void setup() {
        dbHandler = mock(ScaledUnitHandler.class);
        when(dbHandler.commit()).thenReturn(StatusCode.SUCCESS);
        uut = new ScaledUnitManager(dbHandler);
        uut.setPrincipals(TEST_USER);
    }

    @After
    public void tearDown() {
        verify(dbHandler).setPrincipals(TEST_USER);
        verifyNoMoreInteractions(dbHandler);
    }

    @Test
    public void editingWorks() {
        ScaledUnitForEditing data = new ScaledUnitForEditing(1, 2, BigDecimal.ONE, 1);
        when(dbHandler.edit(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.SUCCESS, result);
        verify(dbHandler).edit(data);
        verify(dbHandler).commit();
    }

    @Test
    public void deletingWorks() {
        ScaledUnitForDeletion data = new ScaledUnitForDeletion(1, 2);
        when(dbHandler.delete(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        verify(dbHandler).delete(data);
        verify(dbHandler).commit();
    }
}
