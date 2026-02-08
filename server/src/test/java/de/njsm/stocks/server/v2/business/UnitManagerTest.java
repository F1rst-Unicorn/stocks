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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.UnitForDeletion;
import de.njsm.stocks.common.api.UnitForRenaming;
import de.njsm.stocks.server.v2.db.UnitHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UnitManagerTest {

    private UnitManager uut;

    private UnitHandler dbHandler;

    @BeforeEach
    public void setup() {
        dbHandler = mock(UnitHandler.class);
        when(dbHandler.commit()).thenReturn(StatusCode.SUCCESS);
        uut = new UnitManager(dbHandler);
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(dbHandler);
    }

    @Test
    public void renamingWorks() {
        UnitForRenaming data = UnitForRenaming.builder()
                .id(1)
                .version(2)
                .name("name")
                .abbreviation("abbreviation")
                .build();
        when(dbHandler.rename(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.SUCCESS, result);
        verify(dbHandler).rename(data);
        verify(dbHandler).commit();
    }

    @Test
    public void deletingWorks() {
        UnitForDeletion data = UnitForDeletion.builder()
                .id(1)
                .version(2)
                .build();
        when(dbHandler.delete(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        verify(dbHandler).delete(data);
        verify(dbHandler).commit();
    }
}
