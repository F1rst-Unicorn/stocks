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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.UnitForDeletion;
import de.njsm.stocks.common.api.UnitForInsertion;
import de.njsm.stocks.common.api.UnitForRenaming;
import de.njsm.stocks.server.v2.business.UnitManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnitEndpointTest {

    private UnitEndpoint uut;

    private UnitManager manager;

    @Before
    public void setup() {
        manager = Mockito.mock(UnitManager.class);
        uut = new UnitEndpoint(manager);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test
    public void puttingInvalidNameIsRejected() {
        Response response = uut.put(createMockRequest(), "", "abbreviation");

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void puttingInvalidAbbreviationIsRejected() {
        Response response = uut.put(createMockRequest(), "name", "");

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void validPuttingIsDone() {
        UnitForInsertion input = new UnitForInsertion("name", "abbreviation");
        when(manager.add(any())).thenReturn(StatusCode.SUCCESS);

        Response response = uut.put(createMockRequest(), input.getName(), input.getAbbreviation());

        assertEquals(StatusCode.SUCCESS, response.getStatus());
        verify(manager).add(input);
        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void invalidBusinessPuttingIsPropagated() {
        UnitForInsertion input = new UnitForInsertion("name", "abbreviation");
        when(manager.add(any())).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        Response response = uut.put(createMockRequest(), input.getName(), input.getAbbreviation());

        assertEquals(StatusCode.DATABASE_UNREACHABLE, response.getStatus());
        verify(manager).add(input);
        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void renamingInvalidIdIsRejected() {
        Response response = uut.rename(createMockRequest(), -1, 0, "name", "abbreviation");

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void renamingInvalidVersionIsRejected() {
        Response response = uut.rename(createMockRequest(), 1, -1, "name", "abbreviation");

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void renamingInvalidNameIsRejected() {
        Response response = uut.rename(createMockRequest(), 1, 0, "", "abbreviation");

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void renamingInvalidAbbreviationIsRejected() {
        Response response = uut.rename(createMockRequest(), 1, 0, "name", "");

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void invalidBusinessRenamingIsPropagated() {
        UnitForRenaming input = new UnitForRenaming(1, 1, "name", "abbreviation");
        when(manager.rename(any())).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        Response response = uut.rename(createMockRequest(), input.id(), input.version(), input.getName(), input.getAbbreviation());

        assertEquals(StatusCode.DATABASE_UNREACHABLE, response.getStatus());
        verify(manager).rename(input);
        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void validRenamingWorks() {
        UnitForRenaming input = new UnitForRenaming(1, 1, "name", "abbreviation");
        when(manager.rename(any())).thenReturn(StatusCode.SUCCESS);

        Response response = uut.rename(createMockRequest(), input.id(), input.version(), input.getName(), input.getAbbreviation());

        assertEquals(StatusCode.SUCCESS, response.getStatus());
        verify(manager).rename(input);
        verify(manager).setPrincipals(TEST_USER);
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
