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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.db.CrudDatabaseHandler;
import fj.data.Validation;
import org.jooq.TableRecord;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.stream.Stream;

public interface BusinessGettable<U extends TableRecord<U>, T extends Entity<T>> extends BusinessOperations {

    default Validation<StatusCode, Stream<T>> get(
            AsyncResponse r,
            Instant startingFrom,
            Instant upUntil) {
        return runAsynchronously(r, () -> {
            var result = getDbHandler().setReadOnly();
            if (result.isFail()) {
                return Validation.fail(result);
            }
            return getDbHandler().get(startingFrom, upUntil);
        });
    }

    CrudDatabaseHandler<U, T> getDbHandler();
}
