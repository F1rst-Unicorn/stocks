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

import de.njsm.stocks.server.v2.business.EanNumberManager;
import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static de.njsm.stocks.server.v2.business.StatusCode.INVALID_ARGUMENT;
import static de.njsm.stocks.server.v2.business.StatusCode.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EanNumberEndpointTest {

    private EanNumberEndpoint uut;

    private EanNumberManager manager;

    @Before
    public void setup() {
        manager = Mockito.mock(EanNumberManager.class);
        uut = new EanNumberEndpoint(manager);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test
    public void puttingNullCodeIsInvalid() {

        Response result = uut.putEanNumber(null, 1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void puttingEmptyCodeIsInvalid() {

        Response result = uut.putEanNumber("", 1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void puttingInvalidFoodIdIsInvalid() {

        Response result = uut.putEanNumber("code", 0);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deletingInvalidIdIsInvalid() {

        Response result = uut.deleteEanNumber(0, 1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deletingInvalidVersionIsInvalid() {

        Response result = uut.deleteEanNumber(1, -1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void eanNumberIsAdded() {
        EanNumber data = new EanNumber("CODE", 2);
        when(manager.add(data)).thenReturn(Validation.success(5));

        Response response = uut.putEanNumber(data.eanCode, data.identifiesFood);

        assertEquals(SUCCESS, response.status);
        verify(manager).add(data);
    }

    @Test
    public void getEanNumberReturnsList() {
        List<EanNumber> data = Collections.singletonList(new EanNumber("CODE", 2));
        when(manager.get()).thenReturn(Validation.success(data));

        ListResponse<EanNumber> response = uut.getEanNumbers();

        assertEquals(SUCCESS, response.status);
        assertEquals(data, response.data);
        verify(manager).get();
    }

    @Test
    public void deleteEanNumberWorks() {
        EanNumber data = new EanNumber(1, 0, "", 0);
        when(manager.delete(data)).thenReturn(SUCCESS);

        Response response = uut.deleteEanNumber(data.id, data.version);

        assertEquals(SUCCESS, response.status);
        verify(manager).delete(data);
    }
}