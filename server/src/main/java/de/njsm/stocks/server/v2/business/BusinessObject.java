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

import de.njsm.stocks.server.v2.db.FailSafeDatabaseHandler;
import fj.data.Validation;
import io.prometheus.client.Summary;
import org.glassfish.jersey.internal.util.Producer;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;

public class BusinessObject {

    private static final Summary OPERATION_REPETITIONS = Summary.build()
            .name("stocks_operation_repetitions")
            .help("Count number of repetitions for operation due to serialisation")
            .register();

    private final FailSafeDatabaseHandler dbHandler;

    public BusinessObject(FailSafeDatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    <O> Validation<StatusCode, O> runFunction(Producer<Validation<StatusCode, O>> operation) {
        return runTransactionUntilSerialisable(operation);
    }

    StatusCode runOperation(Producer<StatusCode> operation) {
        Validation<StatusCode, StatusCode> r = runTransactionUntilSerialisable(() -> operation.call().toValidation());
        return StatusCode.toCode(r);
    }

    private <O> Validation<StatusCode, O> runTransactionUntilSerialisable(Producer<Validation<StatusCode, O>> operation) {
        Validation<StatusCode, O> result;
        int repetitions = 0;
        do {
            result = operation.call();
            repetitions++;
            result = finishTransaction(result);
        } while (result.isFail() && result.fail() == StatusCode.SERIALISATION_CONFLICT);
        OPERATION_REPETITIONS.observe(repetitions);
        return result;
    }

    /*
     * Asynchronous operations cannot be repeated as the result is only
     * committed once the request has finished. Then the result has already been
     * reported to the client and there is no point in repeating it.
     */
    <O> Validation<StatusCode, O> runAsynchronously(AsyncResponse r, Producer<Validation<StatusCode, O>> operation) {
        r.register((CompletionCallback) this::finishTransaction);
        return operation.call();
    }

    <O> Validation<StatusCode, O> finishTransaction(Validation<StatusCode, O> carry) {
        if (carry.isFail()) {
            dbHandler.rollback();
            return carry;
        } else {
            StatusCode next = dbHandler.commit();
            if (next.isFail()) {
                return Validation.fail(next);
            } else {
                return carry;
            }
        }
    }

    void finishTransaction(Throwable t) {
        if (t != null)
            dbHandler.rollback();
        else
            dbHandler.commit();
    }
}
