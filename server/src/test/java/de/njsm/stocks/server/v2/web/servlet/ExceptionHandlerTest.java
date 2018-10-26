package de.njsm.stocks.server.v2.web.servlet;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.web.data.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;

public class ExceptionHandlerTest {

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Exception exception;

    private ExceptionHandler uut;

    @Before
    public void setup() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        exception = new Exception("test");

        uut = new ExceptionHandler();

        Mockito.when(request.getAttribute(ExceptionHandler.EXCEPTION_KEY)).thenReturn(exception);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(request);
        Mockito.verifyNoMoreInteractions(response);
    }

    @Test
    public void testGet() {
        Response result = uut.get(request, response);

        assertEquals(StatusCode.GENERAL_ERROR, result.status);
        Mockito.verify(response).setStatus(500);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }

    @Test
    public void testPut() {
        Response result = uut.put(request, response);

        assertEquals(StatusCode.GENERAL_ERROR, result.status);
        Mockito.verify(response).setStatus(500);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }

    @Test
    public void testDelete() {
        Response result = uut.delete(request, response);

        assertEquals(StatusCode.GENERAL_ERROR, result.status);
        Mockito.verify(response).setStatus(500);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }

    @Test
    public void testPost() {
        Response result = uut.post(request, response);

        assertEquals(StatusCode.GENERAL_ERROR, result.status);
        Mockito.verify(response).setStatus(500);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }
}