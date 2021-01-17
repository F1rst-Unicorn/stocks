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
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.business.data.EanNumberForDeletion;
import de.njsm.stocks.server.v2.business.data.EanNumberForGetting;
import de.njsm.stocks.server.v2.business.data.EanNumberForInsertion;
import de.njsm.stocks.server.v2.web.data.Response;
import de.njsm.stocks.server.v2.web.data.StreamResponse;
import fj.data.Validation;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.server.v2.business.StatusCode.INVALID_ARGUMENT;
import static de.njsm.stocks.server.v2.business.StatusCode.SUCCESS;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
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

        Response result = uut.putEanNumber(createMockRequest(), null, 1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void puttingEmptyCodeIsInvalid() {

        Response result = uut.putEanNumber(createMockRequest(), "", 1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void puttingInvalidFoodIdIsInvalid() {

        Response result = uut.putEanNumber(createMockRequest(), "code", 0);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deletingInvalidIdIsInvalid() {

        Response result = uut.deleteEanNumber(createMockRequest(), 0, 1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deletingInvalidVersionIsInvalid() {

        Response result = uut.deleteEanNumber(createMockRequest(), 1, -1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void eanNumberIsAdded() {
        EanNumberForInsertion data = new EanNumberForInsertion(2, "CODE");
        when(manager.add(data)).thenReturn(Validation.success(5));

        Response response = uut.putEanNumber(createMockRequest(), data.getEanNumber(), data.getIdentifiesFood());

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).add(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void getEanNumberReturnsList() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        List<EanNumber> data = Collections.singletonList(new EanNumberForGetting(1, 2, 2, "CODE"));
        when(manager.get(r, false, Instant.EPOCH)).thenReturn(Validation.success(data.stream()));

        uut.get(r, 0, null);

        ArgumentCaptor<StreamResponse<EanNumber>> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(SUCCESS, c.getValue().getStatus());
        assertEquals(data, c.getValue().data.collect(Collectors.toList()));
        verify(manager).get(r, false, Instant.EPOCH);
    }

    @Test
    public void getEanNumbersFromInvalidStartingPoint() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);

        uut.get(r, 1, "invalid");

        ArgumentCaptor<Response> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        TestCase.assertEquals(StatusCode.INVALID_ARGUMENT, c.getValue().getStatus());
    }

    @Test
    public void deleteEanNumberWorks() {
        EanNumberForDeletion data = new EanNumberForDeletion(1, 0);
        when(manager.delete(data)).thenReturn(SUCCESS);

        Response response = uut.deleteEanNumber(createMockRequest(), data.getId(), data.getVersion());

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).delete(data);
        Mockito.verify(manager).setPrincipals(TEST_USER);
    }
}
