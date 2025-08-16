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

package de.njsm.stocks.server.v2.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Path("v2/error")
public class ExceptionHandler {

    private static final Logger LOG = LogManager.getLogger(ExceptionHandler.class);

    static final String EXCEPTION_KEY = "javax.servlet.error.exception";

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

    private Response processError(HttpServletRequest request, HttpServletResponse response) {
        Throwable throwable = (Throwable) request.getAttribute(EXCEPTION_KEY);

        if (inputInstantiationFailed(throwable)) {
            LOG.debug("Caught exception leaving web app", throwable);
            LOG.info("invalid input: " + throwable.getCause().getMessage());
            return setErrorStatus(response, StatusCode.INVALID_ARGUMENT);
        } else {
            LOG.error("Caught exception leaving web app", throwable);
            return setErrorStatus(response, StatusCode.GENERAL_ERROR);
        }
    }

    private boolean inputInstantiationFailed(Throwable throwable) {
        return throwable.getCause() instanceof IllegalStateException ||
                throwable.getCause() instanceof JsonProcessingException;
    }

    private Response setErrorStatus(HttpServletResponse response, StatusCode invalidArgument) {
        response.setStatus(invalidArgument.toHttpStatus().getStatusCode());
        return new Response(invalidArgument);
    }
}
