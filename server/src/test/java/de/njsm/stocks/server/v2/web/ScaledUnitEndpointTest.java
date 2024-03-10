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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.ScaledUnitForDeletion;
import de.njsm.stocks.common.api.ScaledUnitForEditing;
import de.njsm.stocks.common.api.ScaledUnitForInsertion;
import de.njsm.stocks.server.v2.business.ScaledUnitManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScaledUnitEndpointTest {

    private ScaledUnitEndpoint uut;

    private ScaledUnitManager manager;

    @BeforeEach
    public void setup() {
        manager = Mockito.mock(ScaledUnitManager.class);
        uut = new ScaledUnitEndpoint(manager);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test
    public void puttingNullScaleIsRejected() {
        assertThrows(IllegalStateException.class, () ->  uut.put(createMockRequest(), null, 1));
        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void puttingInvalidScaleIsRejected() {
        assertThrows(IllegalStateException.class, () ->  uut.put(createMockRequest(), "hi there", 1));
        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void puttingInvalidUnitIsRejected() {
        assertThrows(IllegalStateException.class, () -> uut.put(createMockRequest(), BigDecimal.ONE.toPlainString(), 0));
        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void validPuttingIsDone() {
        ScaledUnitForInsertion input = ScaledUnitForInsertion.builder()
                .scale(BigDecimal.ONE)
                .unit(1)
                .build();
        when(manager.add(any())).thenReturn(StatusCode.SUCCESS);

        Response response = uut.put(createMockRequest(), input.scale().toPlainString(), input.unit());

        assertEquals(StatusCode.SUCCESS, response.getStatus());
        verify(manager).add(input);
        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void invalidBusinessPuttingIsPropagated() {
        ScaledUnitForInsertion input = ScaledUnitForInsertion.builder()
                .scale(BigDecimal.ONE)
                .unit(1)
                .build();
        when(manager.add(any())).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        Response response = uut.put(createMockRequest(), input.scale().toPlainString(), input.unit());

        assertEquals(StatusCode.DATABASE_UNREACHABLE, response.getStatus());
        verify(manager).add(input);
        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void validEditingWorks() {
        ScaledUnitForEditing data = ScaledUnitForEditing.builder()
                .id(1)
                .version(0)
                .scale(BigDecimal.ONE)
                .unit(2)
                .build();
        when(manager.edit(data)).thenReturn(StatusCode.SUCCESS);

        Response response = uut.edit(createMockRequest(), data.id(), data.version(), data.scale().toString(), data.unit());

        assertEquals(StatusCode.SUCCESS, response.getStatus());
        verify(manager).edit(data);
        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void invalidVersionWhenEditingIsRejected() {
        ScaledUnitForEditing data = ScaledUnitForEditing.builder()
                .id(1)
                .version(999)
                .scale(BigDecimal.ONE)
                .unit(2)
                .build();

        assertThrows(IllegalStateException.class, () -> uut.edit(createMockRequest(), data.id(), -1, data.scale().toString(), data.unit()));

        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void invalidScaleWhenEditingIsRejected() {
        ScaledUnitForEditing data = ScaledUnitForEditing.builder()
                .id(1)
                .version(0)
                .scale(BigDecimal.ONE)
                .unit(2)
                .build();

        assertThrows(IllegalStateException.class, () -> uut.edit(createMockRequest(), data.id(), data.version(), "not a number", data.unit()));

        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void invalidIdWhenEditingIsRejected() {
        ScaledUnitForEditing data = ScaledUnitForEditing.builder()
                .id(999)
                .version(0)
                .scale(BigDecimal.ONE)
                .unit(2)
                .build();

        assertThrows(IllegalStateException.class, () -> uut.edit(createMockRequest(), 0, data.version(), data.scale().toString(), data.unit()));

        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void invalidUnitWhenEditingIsRejected() {
        ScaledUnitForEditing data = ScaledUnitForEditing.builder()
                .id(1)
                .version(0)
                .scale(BigDecimal.ONE)
                .unit(999)
                .build();

        assertThrows(IllegalStateException.class, () -> uut.edit(createMockRequest(), data.id(), data.version(), data.scale().toString(), 0));

        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void wrappingDeletionParameterWorks() {
        int id = 1;
        int version = 2;

        ScaledUnitForDeletion result = uut.wrapParameters(id, version);

        assertEquals(id, result.id());
        assertEquals(version, result.version());
    }
}
