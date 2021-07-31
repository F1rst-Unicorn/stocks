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

package de.njsm.stocks.server.v2.web;


import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.StreamResponse;
import de.njsm.stocks.common.api.impl.Update;
import de.njsm.stocks.server.v2.business.UpdateManager;
import fj.data.Validation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.stream.Stream;

@Path("/v2/update")
public class UpdateEndpoint {

    private final UpdateManager handler;

    @Inject
    public UpdateEndpoint(UpdateManager handler) {
        this.handler = handler;
    }

    @GET
    @Produces("application/json")
    public void getUpdates(@Suspended AsyncResponse r) {
        Validation<StatusCode, Stream<Update>> result = handler.getUpdates(r);
        r.resume(new StreamResponse<>(result));
    }
}
