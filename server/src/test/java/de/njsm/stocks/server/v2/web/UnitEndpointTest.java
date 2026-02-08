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

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.business.UnitManager;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnitEndpointTest {

    private UnitEndpoint uut;

    private UnitManager manager;

    @BeforeEach
    public void setup() {
        manager = Mockito.mock(UnitManager.class);
        uut = new UnitEndpoint(manager);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test
    public void puttingInvalidNameIsRejected() {
        Response response = uut.put("", "abbreviation");

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void puttingInvalidAbbreviationIsRejected() {
        Response response = uut.put("name", "");

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void validPuttingIsDone() {
        UnitForInsertion input = UnitForInsertion.builder()
                .name("name")
                .abbreviation("abbreviation")
                .build();
        when(manager.addReturningId(any())).thenReturn(Validation.success(1));

        Response response = uut.put(input.name(), input.abbreviation());

        assertEquals(StatusCode.SUCCESS, response.getStatus());
        verify(manager).addReturningId(input);
    }

    @Test
    public void invalidBusinessPuttingIsPropagated() {
        UnitForInsertion input = UnitForInsertion.builder()
                .name("name")
                .abbreviation("abbreviation")
                .build();
        when(manager.addReturningId(any())).thenReturn(Validation.fail(StatusCode.DATABASE_UNREACHABLE));

        Response response = uut.put(input.name(), input.abbreviation());

        assertEquals(StatusCode.DATABASE_UNREACHABLE, response.getStatus());
        verify(manager).addReturningId(input);
    }

    @Test
    public void renamingInvalidIdIsRejected() {
        Response response = uut.rename(-1, 0, "name", "abbreviation");

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void renamingInvalidVersionIsRejected() {
        Response response = uut.rename(1, -1, "name", "abbreviation");

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void renamingInvalidNameIsRejected() {
        Response response = uut.rename(1, 0, "", "abbreviation");

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void renamingInvalidAbbreviationIsRejected() {
        Response response = uut.rename(1, 0, "name", "");

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void invalidBusinessRenamingIsPropagated() {
        UnitForRenaming input = UnitForRenaming.builder()
                .id(1)
                .version(1)
                .name("name")
                .abbreviation("abbreviation")
                .build();
        when(manager.rename(any())).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        Response response = uut.rename(input.id(), input.version(), input.name(), input.abbreviation());

        assertEquals(StatusCode.DATABASE_UNREACHABLE, response.getStatus());
        verify(manager).rename(input);
    }

    @Test
    public void validRenamingWorks() {
        UnitForRenaming input = UnitForRenaming.builder()
                .id(1)
                .version(1)
                .name("name")
                .abbreviation("abbreviation")
                .build();
        when(manager.rename(any())).thenReturn(StatusCode.SUCCESS);

        Response response = uut.rename(input.id(), input.version(), input.name(), input.abbreviation());

        assertEquals(StatusCode.SUCCESS, response.getStatus());
        verify(manager).rename(input);
    }


    @Test
    public void wrappingDeletionParameterWorks() {
        int id = 1;
        int version = 2;

        UnitForDeletion result = uut.wrapParameters(id, version);

        assertEquals(id, result.id());
        assertEquals(version, result.version());
    }
}
