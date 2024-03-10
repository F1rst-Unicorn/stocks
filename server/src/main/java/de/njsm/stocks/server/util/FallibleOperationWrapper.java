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

package de.njsm.stocks.server.util;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface FallibleOperationWrapper<I, E extends Exception> {

    Logger LOG = LogManager.getLogger(FallibleOperationWrapper.class);

    default StatusCode runCommand(FunctionWithExceptions<I, StatusCode, E> client) {
        Validation<StatusCode, StatusCode> result = runFunction(input -> Validation.fail(client.apply(input)));
        return result.fail();
    }

    default <O> Validation<StatusCode, O> runFunction(FunctionWithExceptions<I, Validation<StatusCode, O>, E> function) {
        try {
            return wrap(function).accept();
        } catch (Exception e) {
            LOG.error("wrapped operation failed", e);
            return Validation.fail(getDefaultErrorCode());
        }
    }

    StatusCode getDefaultErrorCode();

    <O> ProducerWithExceptions<Validation<StatusCode, O>, E>
    wrap(FunctionWithExceptions<I, Validation<StatusCode, O>, E> client);
}
