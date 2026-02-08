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


import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.Versionable;
import jakarta.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static de.njsm.stocks.server.v2.web.Endpoint.isValid;
import static de.njsm.stocks.server.v2.web.Endpoint.isValidVersion;

public interface Delete<T extends Versionable<U>, U extends Entity<U>> extends MetaDelete<T, U> {

    @DeleteMapping(produces = MediaType.APPLICATION_JSON)
    default Response delete(@RequestParam("id") int id,
                            @RequestParam("version") int version) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {
            return delete(() -> wrapParameters(id, version));
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    T wrapParameters(int id, int version);
}
