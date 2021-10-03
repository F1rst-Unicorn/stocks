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
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import fj.data.Validation;
import io.prometheus.client.Counter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface HystrixWrapper<I, E extends Exception> {

    static final Counter CIRCUIT_BREAKER_EVENTS = Counter.build()
            .name("stocks_circuit_breaker_trigger")
            .labelNames("resource")
            .help("Number of circuit breaker open events")
            .register();

    Logger LOG = LogManager.getLogger(HystrixWrapper.class);

    default StatusCode runCommand(FunctionWithExceptions<I, StatusCode, E> client) {
        Validation<StatusCode, StatusCode> result = runFunction(input -> Validation.fail(client.apply(input)));
        return result.fail();
    }

    default <O> Validation<StatusCode, O> runFunction(FunctionWithExceptions<I, Validation<StatusCode, O>, E> function) {
        HystrixProducer<I, Validation<StatusCode, O>, E> producer = new HystrixProducer<>(getResourceIdentifier(),
                getCircuitBreakerTimeout(),
                this::wrap,
                function);

        try {
            return producer.execute();
        } catch (HystrixRuntimeException e) {
            LOG.error("circuit breaker '{}' has error: {}", getResourceIdentifier(), e.getFailureType());

            if (e.getFailureType() == HystrixRuntimeException.FailureType.COMMAND_EXCEPTION ||
                    e.getFailureType() == HystrixRuntimeException.FailureType.BAD_REQUEST_EXCEPTION)
                LOG.error("", e);
            else
                LOG.debug("", e);

            CIRCUIT_BREAKER_EVENTS.labels(getResourceIdentifier()).inc();
            return Validation.fail(getDefaultErrorCode());
        }
    }

    String getResourceIdentifier();

    int getCircuitBreakerTimeout();

    StatusCode getDefaultErrorCode();

    <O> ProducerWithExceptions<Validation<StatusCode, O>, E>
    wrap(FunctionWithExceptions<I, Validation<StatusCode, O>, E> client);
}
