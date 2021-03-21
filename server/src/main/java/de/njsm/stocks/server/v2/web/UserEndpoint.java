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

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.UserManager;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserForDeletion;
import de.njsm.stocks.server.v2.business.data.UserForInsertion;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserRecord;
import de.njsm.stocks.server.v2.web.data.DataResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

@Path("/v2/user")
public class UserEndpoint extends Endpoint implements Get<UserRecord, User>, Delete<UserForDeletion, User> {

    private final UserManager manager;

    @Inject
    public UserEndpoint(UserManager manager) {
        this.manager = manager;
    }

    @PUT
    @Produces("application/json")
    public Response putUser(@Context HttpServletRequest request,
                            @QueryParam("name") String name) {

        if (isValidName(name, "name")) {
            manager.setPrincipals(getPrincipals(request));
            StatusCode result = manager.addUser(new UserForInsertion(name));
            return new Response(result);

        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }

    @Override
    public UserManager getManager() {
        return manager;
    }

    @Override
    public UserForDeletion wrapParameters(int id, int version) {
        return new UserForDeletion(id, version);
    }
}
