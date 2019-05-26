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

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.UpdateManager;
import de.njsm.stocks.server.v2.business.data.Update;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class UpdateEndpointTest {

    private UpdateEndpoint uut;

    private UpdateManager dbLayer;

    @Before
    public void setup() {
        dbLayer = Mockito.mock(UpdateManager.class);
        uut = new UpdateEndpoint(dbLayer);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(dbLayer);
    }

    @Test
    public void getUpdates() {
        Mockito.when(dbLayer.getUpdates())
                .thenReturn(Validation.success(new LinkedList<>()));

        ListResponse<Update> result = uut.getUpdates();

        assertEquals(StatusCode.SUCCESS, result.status);
        assertEquals(0, result.data.size());
        Mockito.verify(dbLayer).getUpdates();
    }


}