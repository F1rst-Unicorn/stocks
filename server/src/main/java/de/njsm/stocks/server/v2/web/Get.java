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
import de.njsm.stocks.common.api.StreamResponse;
import de.njsm.stocks.server.v2.business.BusinessGettable;
import fj.data.Validation;
import org.jooq.TableRecord;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.Endpoint.parseToInstant;

public interface Get<U extends TableRecord<U>, T extends Entity<T>> {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    default void get(@Suspended AsyncResponse response,
                    @QueryParam("startingFrom") String startingFromParameter,
                     @QueryParam("upUntil") String upUntilParameter) {
        Optional<Instant> startingFrom = parseToInstant(startingFromParameter, "startingFrom");
        Optional<Instant> upUntil = parseToInstant(upUntilParameter, "upUntil");
        if (startingFrom.isEmpty() || upUntil.isEmpty()) {
            response.resume(new Response(StatusCode.INVALID_ARGUMENT));
            return;
        }

        Validation<StatusCode, Stream<T>> result = getManager().get(
                response,
                startingFrom.get(),
                upUntil.get());
        response.resume(new StreamResponse<>(result));
    }

    BusinessGettable<U, T> getManager();
}
