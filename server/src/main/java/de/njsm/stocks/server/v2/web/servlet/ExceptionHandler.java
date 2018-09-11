package de.njsm.stocks.server.v2.web.servlet;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.web.data.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("v2/error")
public class ExceptionHandler {

    private static final Logger LOG = LogManager.getLogger(ExceptionHandler.class);

    private static final String EXCEPTION_KEY = "javax.servlet.error.exception";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest request,
                        @Context HttpServletResponse response) {
        return processError(request, response);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response put(@Context HttpServletRequest request,
                        @Context HttpServletResponse response) {
        return processError(request, response);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@Context HttpServletRequest request,
                        @Context HttpServletResponse response) {
        return processError(request, response);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(@Context HttpServletRequest request,
                        @Context HttpServletResponse response) {
        return processError(request, response);
    }

    private Response processError(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        Throwable throwable = (Throwable) request.getAttribute(EXCEPTION_KEY);

        LOG.error("Caught exception leaving web app", throwable);

        response.setStatus(500);
        return new Response(StatusCode.GENERAL_ERROR);
    }

}
