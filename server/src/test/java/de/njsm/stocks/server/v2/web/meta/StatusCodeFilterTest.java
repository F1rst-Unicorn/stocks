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

package de.njsm.stocks.server.v2.web.meta;

import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.ServerHttpResponse;

public class StatusCodeFilterTest {

    private final StatusCodeToHttpCodeFilter uut = new StatusCodeToHttpCodeFilter();

    private ServerHttpResponse response = Mockito.mock(ServerHttpResponse.class);

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(response);
    }

    @Test
    public void missingEntityPassesThrough() {
        uut.beforeBodyWrite(null, null, null, null, null, response);
    }

    @Test
    public void statusCodeAffectsHttpStatus() {
        uut.beforeBodyWrite(new Response(StatusCode.NOT_FOUND), null, null, null, null, response);

        Mockito.verify(response).setStatusCode(HttpStatusCode.valueOf(404));
    }
}
