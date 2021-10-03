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

package de.njsm.stocks.server.v2.web.servlet;

import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.ContainerResponseContext;
import java.io.IOException;

public class StatusCodeFilterTest {

    private StatusCodeFilter uut = new StatusCodeFilter();

    private ContainerResponseContext context = Mockito.mock(ContainerResponseContext.class);

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(context);
    }

    @Test
    public void missingEntityPassesThrough() throws IOException {
        Mockito.when(context.getEntity()).thenReturn(null);
        uut.filter(null, context);
        Mockito.verify(context).getEntity();
    }

    @Test
    public void statusCodeAffectsHttpStatus() throws IOException {
        Mockito.when(context.getEntity()).thenReturn(new Response(StatusCode.NOT_FOUND));
        Mockito.when(context.getStatus()).thenReturn(200);
        uut.filter(null, context);

        Mockito.verify(context).getEntity();
        Mockito.verify(context).setStatus(StatusCode.NOT_FOUND.toHttpStatus().getStatusCode());
        Mockito.verify(context, Mockito.times(2)).getStatus();
    }

    @Test
    public void loggingNotHappeningIfNothingChanges() throws IOException {
        Mockito.when(context.getEntity()).thenReturn(new Response(StatusCode.NOT_FOUND));
        Mockito.when(context.getStatus()).thenReturn(404);
        uut.filter(null, context);

        Mockito.verify(context).getEntity();
        Mockito.verify(context).setStatus(StatusCode.NOT_FOUND.toHttpStatus().getStatusCode());
        Mockito.verify(context).getStatus();
    }
}
