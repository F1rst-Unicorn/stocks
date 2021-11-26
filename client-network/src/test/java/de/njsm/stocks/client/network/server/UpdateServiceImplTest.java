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

package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.entities.StatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateServiceImplTest {

    private UpdateServiceImpl uut;

    private Api api;

    private CallHandler callHandler;

    @BeforeEach
    void setUp() {
        api = mock(Api.class);
        callHandler = mock(CallHandler.class);
        uut = new UpdateServiceImpl(api, callHandler);
    }

    @Test
    void exceptionIsForwardedAsStatusCodeException() {
        StatusCodeException expected = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        when(api.getUpdates()).thenReturn(null);
        when(callHandler.executeForResult(null)).thenThrow(expected);

        uut.getUpdates().test().assertError(expected);
    }
}
