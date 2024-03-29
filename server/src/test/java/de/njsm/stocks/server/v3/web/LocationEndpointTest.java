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

package de.njsm.stocks.server.v3.web;

import de.njsm.stocks.common.api.DataResponse;
import de.njsm.stocks.common.api.LocationForInsertion;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.business.LocationManager;
import fj.data.Validation;
import org.junit.jupiter.api.Test;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LocationEndpointTest {

    @Test
    void requestIsForwarded() {
        LocationForInsertion input = LocationForInsertion.builder()
                .name("Fridge")
                .description("the cold one")
                .build();
        LocationManager manager = mock(LocationManager.class);
        when(manager.addReturningId(input)).thenReturn(Validation.success(1));
        LocationEndpoint uut = new LocationEndpoint(manager);

        DataResponse<Integer> actual = uut.put(createMockRequest(), input);

        assertEquals(StatusCode.SUCCESS, actual.getStatus());
        assertEquals(1, actual.getData());
        verify(manager).addReturningId(input);
        verify(manager).setPrincipals(TEST_USER);
        verifyNoMoreInteractions(manager);
    }
}
