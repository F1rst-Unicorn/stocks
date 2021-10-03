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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.db.FailSafeDatabaseHandler;
import fj.data.Validation;
import org.glassfish.jersey.internal.util.Producer;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;

public interface AsyncRunner {

    /*
     * Asynchronous operations cannot be repeated as the result is only
     * committed once the request has finished. Then the result has already been
     * reported to the client and there is no point in repeating it.
     */
    default <O> Validation<StatusCode, O> runAsynchronously(AsyncResponse r, Producer<Validation<StatusCode, O>> operation) {
        r.register((CompletionCallback) this::finishTransaction);
        return operation.call();
    }

    default void finishTransaction(Throwable t) {
        if (t != null)
            getDbHandler().rollback();
        else
            getDbHandler().commit();
    }

    FailSafeDatabaseHandler getDbHandler();
}
