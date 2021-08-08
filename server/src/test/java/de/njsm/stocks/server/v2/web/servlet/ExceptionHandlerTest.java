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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import org.glassfish.jersey.server.ContainerException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExceptionHandlerTest {

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Exception exception;

    private ExceptionHandler uut;

    @BeforeEach
    public void setup() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        exception = new Exception("test");

        uut = new ExceptionHandler();

        Mockito.when(request.getAttribute(ExceptionHandler.EXCEPTION_KEY)).thenReturn(exception);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(request);
        Mockito.verifyNoMoreInteractions(response);
    }

    @Test
    public void testGet() {
        Response result = uut.get(request, response);

        assertEquals(StatusCode.GENERAL_ERROR, result.getStatus());
        Mockito.verify(response).setStatus(500);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }

    @Test
    public void testPut() {
        Response result = uut.put(request, response);

        assertEquals(StatusCode.GENERAL_ERROR, result.getStatus());
        Mockito.verify(response).setStatus(500);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }

    @Test
    public void testDelete() {
        Response result = uut.delete(request, response);

        assertEquals(StatusCode.GENERAL_ERROR, result.getStatus());
        Mockito.verify(response).setStatus(500);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }

    @Test
    public void testPost() {
        Response result = uut.post(request, response);

        assertEquals(StatusCode.GENERAL_ERROR, result.getStatus());
        Mockito.verify(response).setStatus(500);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }

    @Test
    public void valueInstantiationExceptionYieldsInvalidInput() {
        JsonMappingException nestedException = ValueInstantiationException.from((JsonParser) null, "test");
        ContainerException exception = new ContainerException(nestedException);
        Mockito.when(request.getAttribute(ExceptionHandler.EXCEPTION_KEY)).thenReturn(exception);

        Response result = uut.put(request, response);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
        Mockito.verify(response).setStatus(400);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }
}
