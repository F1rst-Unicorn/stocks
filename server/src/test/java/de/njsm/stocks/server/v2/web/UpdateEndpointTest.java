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

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.StreamResponse;
import de.njsm.stocks.common.api.Update;
import de.njsm.stocks.server.v2.business.UpdateManager;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class UpdateEndpointTest {

    private UpdateEndpoint uut;

    private UpdateManager dbLayer;

    @BeforeEach
    public void setup() {
        dbLayer = Mockito.mock(UpdateManager.class);
        uut = new UpdateEndpoint(dbLayer);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(dbLayer);
    }

    @Test
    public void getUpdates() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(dbLayer.getUpdates(r))
                .thenReturn(Validation.success(Stream.of()));

        uut.getUpdates(r);

        ArgumentCaptor<StreamResponse<Update>> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(StatusCode.SUCCESS, c.getValue().getStatus());
        assertEquals(0, c.getValue().data.count());
        Mockito.verify(dbLayer).getUpdates(r);
    }


}
