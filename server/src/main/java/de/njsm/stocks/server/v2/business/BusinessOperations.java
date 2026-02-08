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

import java.util.function.Supplier;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.db.FailSafeDatabaseHandler;
import fj.data.Validation;

public interface BusinessOperations {

    default <O> Validation<StatusCode, O> runFunction(Supplier<Validation<StatusCode, O>> operation) {
        return runTransactionUntilSerialisable(operation);
    }

    default StatusCode runOperation(Supplier<StatusCode> operation) {
        Validation<StatusCode, StatusCode> r = runTransactionUntilSerialisable(() -> operation.get().toValidation());
        return StatusCode.toCode(r);
    }

    default <O> Validation<StatusCode, O> runTransactionUntilSerialisable(Supplier<Validation<StatusCode, O>> operation) {
        Validation<StatusCode, O> result;
        do {
            result = operation.get();
            result = finishTransaction(result);
        } while (result.isFail() && result.fail() == StatusCode.SERIALISATION_CONFLICT);
        return result;
    }

    default <O> Validation<StatusCode, O> finishTransaction(Validation<StatusCode, O> carry) {
        if (carry.isFail()) {
            getDbHandler().rollback();
            return carry;
        } else {
            StatusCode next = getDbHandler().commit();
            if (next.isFail()) {
                return Validation.fail(next);
            } else {
                return carry;
            }
        }
    }

    FailSafeDatabaseHandler getDbHandler();
}
