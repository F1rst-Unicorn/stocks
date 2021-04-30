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

import de.njsm.stocks.server.v2.business.ScaledUnitManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.ScaledUnitForInsertion;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScaledUnitEndpointTest {

    private ScaledUnitEndpoint uut;

    private ScaledUnitManager manager;

    @Before
    public void setup() {
        manager = Mockito.mock(ScaledUnitManager.class);
        uut = new ScaledUnitEndpoint(manager);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test
    public void puttingInvalidScaleIsRejected() {
        Response response = uut.put(createMockRequest(), "hi there", 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void puttingInvalidUnitIsRejected() {
        Response response = uut.put(createMockRequest(), BigDecimal.ONE.toPlainString(), 0);

        assertEquals(StatusCode.INVALID_ARGUMENT, response.getStatus());
    }

    @Test
    public void validPuttingIsDone() {
        ScaledUnitForInsertion input = new ScaledUnitForInsertion(BigDecimal.ONE, 1);
        when(manager.add(any())).thenReturn(Validation.success(1));

        Response response = uut.put(createMockRequest(), input.getScale().toPlainString(), input.getUnit());

        assertEquals(StatusCode.SUCCESS, response.getStatus());
        verify(manager).add(input);
        verify(manager).setPrincipals(TEST_USER);
    }

    @Test
    public void invalidBusinessPuttingIsPropagated() {
        ScaledUnitForInsertion input = new ScaledUnitForInsertion(BigDecimal.ONE, 1);
        when(manager.add(any())).thenReturn(Validation.fail(StatusCode.DATABASE_UNREACHABLE));

        Response response = uut.put(createMockRequest(), input.getScale().toPlainString(), input.getUnit());

        assertEquals(StatusCode.DATABASE_UNREACHABLE, response.getStatus());
        verify(manager).add(input);
        verify(manager).setPrincipals(TEST_USER);
    }
}
