package de.njsm.stocks.server.v2.web.servlet;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.web.data.Response;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.ContainerResponseContext;
import java.io.IOException;

public class StatusCodeFilterTest {

    private StatusCodeFilter uut = new StatusCodeFilter();

    private ContainerResponseContext context = Mockito.mock(ContainerResponseContext.class);

    @After
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