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

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.db.ScaledUnitHandler;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ScaledUnitManagerTest {

    private ScaledUnitManager uut;

    private ScaledUnitHandler dbHandler;

    @BeforeEach
    public void setup() {
        dbHandler = mock(ScaledUnitHandler.class);
        when(dbHandler.commit()).thenReturn(StatusCode.SUCCESS);
        uut = new ScaledUnitManager(dbHandler);
        uut.setPrincipals(TEST_USER);
    }

    @AfterEach
    public void tearDown() {
        verify(dbHandler).setPrincipals(TEST_USER);
        verifyNoMoreInteractions(dbHandler);
    }

    @Test
    public void editingWorks() {
        ScaledUnitForEditing data = ScaledUnitForEditing.builder()
                .id(1)
                .version(2)
                .scale(BigDecimal.ONE)
                .unit(1)
                .build();
        when(dbHandler.edit(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.SUCCESS, result);
        verify(dbHandler).edit(data);
        verify(dbHandler).commit();
    }

    @Test
    public void deletingWorks() {
        ScaledUnitForDeletion data = ScaledUnitForDeletion.builder()
                .id(1)
                .version(2)
                .build();
        when(dbHandler.delete(data)).thenReturn(StatusCode.SUCCESS);
        when(dbHandler.get(false, Instant.EPOCH)).thenReturn(Validation.success(Stream.of(ScaledUnitForGetting.builder()
                .id(1)
                .version(2)
                .unit(1)
                .scale(BigDecimal.ONE)
                .build(),
                ScaledUnitForGetting.builder()
                        .id(2)
                        .version(2)
                        .unit(1)
                        .scale(BigDecimal.ONE)
                        .build())));

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        verify(dbHandler).get(false, Instant.EPOCH);
        verify(dbHandler).delete(data);
        verify(dbHandler).commit();
    }

    @Test
    public void deletingLastScaledUnitIsDenied() {
        ScaledUnitForDeletion data = ScaledUnitForDeletion.builder()
                .id(1)
                .version(2)
                .build();
        when(dbHandler.get(false, Instant.EPOCH)).thenReturn(Validation.success(Stream.of(ScaledUnitForGetting.builder()
                .id(1)
                .version(2)
                .unit(1)
                .scale(BigDecimal.ONE)
                .build())));

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.ACCESS_DENIED, result);
        verify(dbHandler).get(false, Instant.EPOCH);
        verify(dbHandler).rollback();
    }
}
