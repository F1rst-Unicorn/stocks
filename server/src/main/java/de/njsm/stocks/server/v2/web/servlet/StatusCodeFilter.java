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

import de.njsm.stocks.server.v2.web.data.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class StatusCodeFilter implements ContainerResponseFilter {

    private static final Logger LOG = LogManager.getLogger(StatusCodeFilter.class);

    @Override
    public void filter(ContainerRequestContext crc,
                       ContainerResponseContext responseContext) throws IOException {
        Object entity = responseContext.getEntity();
        if (entity instanceof Response) {
            Response r = (Response) entity;
            javax.ws.rs.core.Response.Status code = r.status.toHttpStatus();
            if (code.getStatusCode() != responseContext.getStatus()) {
                LOG.debug("HTTP status set from {} to {}", responseContext.getStatusInfo().getReasonPhrase(), code);
            }
            responseContext.setStatus(code.getStatusCode());
        }
    }
}
