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

package de.njsm.stocks.client.network;

import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.io.IOException;

import static de.njsm.stocks.common.api.StatusCode.GENERAL_ERROR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CallHandlerTest {

    private CallHandler uut;

    private Call<Response> call;

    @BeforeEach
    void setUp() {
        uut = new CallHandler();
        call = mock(Call.class);
    }

    @Test
    void ioExceptionGivesGeneralError() throws IOException {
        when(call.execute()).thenThrow(IOException.class);

        StatusCode actual = uut.executeCommand(call);

        assertThat(actual, is(GENERAL_ERROR));
    }
}
