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

    private FailSafeDatabaseHandler dbHandler;

    public BusinessObject(FailSafeDatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    <O> Validation<StatusCode, O> runFunction(Producer<Validation<StatusCode, O>> operation) {
        Validation<StatusCode, O> result = getOs(operation);
        return finishTransaction(result);
    }

    <O> Validation<StatusCode, O> runFunction(AsyncResponse r, Producer<Validation<StatusCode, O>> operation) {
        r.register((CompletionCallback) t -> finishTransaction(StatusCode.SUCCESS));
        return getOs(operation);
    }

    private <O> Validation<StatusCode, O> getOs(Producer<Validation<StatusCode, O>> operation) {
        Validation<StatusCode, O> result;
        int repetitions = 0;
        do {
            result = operation.call();
            repetitions++;
        } while (result.isFail() && result.fail() == StatusCode.SERIALISATION_CONFLICT);
        OPERATION_REPETITIONS.observe(repetitions);
        return result;
    }

    StatusCode runOperation(Producer<StatusCode> operation) {
        StatusCode result;
        int repetitions = 0;
        do {
            result = operation.call();
            repetitions++;
        } while (result == StatusCode.SERIALISATION_CONFLICT);
        OPERATION_REPETITIONS.observe(repetitions);
        return finishTransaction(result);
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

    StatusCode finishTransaction(StatusCode carry) {
        if (carry.isFail()) {
            dbHandler.rollback();
            return carry;
        } else {
            return dbHandler.commit();
        }
    }


}
