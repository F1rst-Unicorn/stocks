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
import org.glassfish.jersey.internal.util.Producer;

public class BusinessObject {

    private FailSafeDatabaseHandler dbHandler;

    public BusinessObject(FailSafeDatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    <O> Validation<StatusCode, O> runFunction(Producer<Validation<StatusCode, O>> operation) {
        Validation<StatusCode, O> result;
        do {
            result = operation.call();
        } while (result.isFail() && result.fail() == StatusCode.SERIALISATION_CONFLICT);
        return finishTransaction(result);
    }

    StatusCode runOperation(Producer<StatusCode> operation) {
        StatusCode result;
        do {
            result = operation.call();
        } while (result == StatusCode.SERIALISATION_CONFLICT);
        return finishTransaction(result);
    }

    <O> Validation<StatusCode, O> finishTransaction(Validation<StatusCode, O> carry) {
        if (carry.isFail()) {
            dbHandler.rollback();
            return carry;
        } else {
            StatusCode next = dbHandler.commit();
            if (next == StatusCode.SUCCESS) {
                return carry;
            } else {
                return Validation.fail(next);
            }
        }
    }

    StatusCode finishTransaction(StatusCode carry) {
        if (carry != StatusCode.SUCCESS) {
            dbHandler.rollback();
            return carry;
        } else {
            StatusCode next = dbHandler.commit();
            if (next == StatusCode.SUCCESS) {
                return StatusCode.SUCCESS;
            } else {
                return next;
            }
        }
    }


}
