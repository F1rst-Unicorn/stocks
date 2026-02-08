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
import de.njsm.stocks.common.api.ListResponse;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.business.BusinessGettable;
import fj.data.Validation;
import jakarta.ws.rs.core.MediaType;
import org.jooq.TableRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static de.njsm.stocks.server.v2.web.Endpoint.parseToInstant;

public interface Get<U extends TableRecord<U>, T extends Entity<T>> {

    @GetMapping(produces = MediaType.APPLICATION_JSON)
    default Response get(@RequestParam("startingFrom") String startingFromParameter,
                     @RequestParam("upUntil") String upUntilParameter) {
        Optional<Instant> startingFrom = parseToInstant(startingFromParameter, "startingFrom");
        Optional<Instant> upUntil = parseToInstant(upUntilParameter, "upUntil");
        if (startingFrom.isEmpty() || upUntil.isEmpty()) {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }

        Validation<StatusCode, List<T>> result = getManager().get(
                startingFrom.get(),
                upUntil.get());
        return new ListResponse<>(result);
    }

    BusinessGettable<U, T> getManager();
}
