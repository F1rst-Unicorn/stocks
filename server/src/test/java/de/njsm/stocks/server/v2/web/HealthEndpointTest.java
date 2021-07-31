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

import de.njsm.stocks.common.api.DataResponse;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.impl.Health;
import de.njsm.stocks.server.v2.business.HealthManager;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HealthEndpointTest {

    private HealthEndpoint uut;

    private HealthManager businessMock;

    @Before
    public void setup() {
        businessMock = mock(HealthManager.class);
        uut = new HealthEndpoint(businessMock);
    }

    @Test
    public void fineHealthIsReported() {
        when(businessMock.get()).thenReturn(Validation.success(new Health(true, true)));

        DataResponse<Health> output = uut.getStatus();

        assertEquals(StatusCode.SUCCESS, output.getStatus());
        assertTrue(output.data.isDatabase());
        assertTrue(output.data.isCa());
    }

    @Test
    public void failingHealthIsReported() {
        when(businessMock.get()).thenReturn(Validation.fail(StatusCode.GENERAL_ERROR));

        DataResponse<Health> output = uut.getStatus();

        assertEquals(StatusCode.GENERAL_ERROR, output.getStatus());
        assertNull(output.data);
    }
}
