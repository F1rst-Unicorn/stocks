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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExceptionHandlerTest {

    private Exception exception;

    private StatusCodeExceptionHandler uut;

    @BeforeEach
    public void setup() {
        exception = new Exception("test");

        uut = new StatusCodeExceptionHandler();
    }

    @Test
    public void testGet() {
        ResponseEntity<Response> result = uut.handleException(exception);

        assertEquals(StatusCode.GENERAL_ERROR, result.getBody().getStatus());
        assertEquals(HttpStatusCode.valueOf(500), result.getStatusCode());
    }

    @Test
    public void valueInstantiationExceptionYieldsInvalidInput() {
        JsonMappingException nestedException = ValueInstantiationException.from((JsonParser) null, "test");
        Exception exception = new Exception(nestedException);

        ResponseEntity<Response> result = uut.handleException(exception);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getBody().getStatus());
        assertEquals(HttpStatusCode.valueOf(400), result.getStatusCode());
    }

    @Test
    public void nestedIllegalStateExceptionYieldsInvalidInput() {
        IllegalStateException illegalStateException = new IllegalStateException("test");
        JsonMappingException nestedException = ValueInstantiationException.from((JsonParser) null, "test", illegalStateException);
        Exception exception = new Exception(nestedException);

        ResponseEntity<Response> result = uut.handleException(exception);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getBody().getStatus());
        assertEquals(HttpStatusCode.valueOf(400), result.getStatusCode());
    }

    @Test
    public void illegalStateExceptionYieldsInvalidInput() {
        Exception exception = new Exception(new IllegalStateException("test"));

        ResponseEntity<Response> result = uut.handleException(exception);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getBody().getStatus());
        assertEquals(HttpStatusCode.valueOf(400), result.getStatusCode());
    }
}
