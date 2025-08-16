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

package de.njsm.stocks.server.v2.web;


import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.business.UserManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserRecord;
import fj.data.Validation;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.inject.Inject;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;

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
            Validation<StatusCode, Integer> result = manager.addReturningId(UserForInsertion.builder()
                    .name(name)
                    .build());
            return new DataResponse<>(result);

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
        return UserForDeletion.builder()
                .id(id)
                .version(version)
                .build();
    }
}
