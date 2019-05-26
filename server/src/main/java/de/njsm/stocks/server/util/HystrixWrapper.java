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

package de.njsm.stocks.server.util;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface HystrixWrapper<I, E extends Exception> {

    Logger LOG = LogManager.getLogger(HystrixWrapper.class);

    default StatusCode runCommand(FunctionWithExceptions<I, StatusCode, E> client) {
        Validation<StatusCode, StatusCode> result = runFunction(input -> {
            StatusCode code = client.apply(input);
            if (code == StatusCode.SUCCESS) {
                return Validation.success(code);
            } else {
                return Validation.fail(code);
            }
        });
        if (result.isFail()) {
            return result.fail();
        } else {
            return result.success();
        }
    }

    default <O> Validation<StatusCode, O> runFunction(FunctionWithExceptions<I, Validation<StatusCode, O>, E> function) {
        HystrixProducer<I, Validation<StatusCode, O>, E> producer = new HystrixProducer<>(getResourceIdentifier(),
                this::wrap,
                function);

        try {
            return producer.execute();
        } catch (HystrixRuntimeException e) {
            return handleException(e);
        }
    }

    String getResourceIdentifier();

    StatusCode getDefaultErrorCode();

    <O> ProducerWithExceptions<Validation<StatusCode, O>, E>
    wrap(FunctionWithExceptions<I, Validation<StatusCode, O>, E> client);

    default <O> Validation<StatusCode, O> handleException(HystrixRuntimeException e) {
        LOG.error("circuit breaker error", e);
        return Validation.fail(getDefaultErrorCode());
    }

}
