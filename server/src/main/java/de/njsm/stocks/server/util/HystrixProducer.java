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

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HystrixProducer<I, O, E extends Exception> extends HystrixCommand<O> {

    private static final Logger LOG = LogManager.getLogger(HystrixProducer.class);

    private FunctionWithExceptions<I, O, E> client;

    private FunctionWithExceptions<FunctionWithExceptions<I, O, E>, ProducerWithExceptions<O, E>, E> wrapper;

    public HystrixProducer(String resourceIdentifier,
                           int timeout,
                           FunctionWithExceptions<FunctionWithExceptions<I, O, E>, ProducerWithExceptions<O, E>, E> wrapper,
                           FunctionWithExceptions<I, O, E> client) {
        super(getHystrixConfig(resourceIdentifier, timeout));
        this.client = client;
        this.wrapper = wrapper;
    }

    @Override
    protected O run() throws E {
        return wrapper.apply(client).accept();
    }

    private static Setter getHystrixConfig(String identifier, int timeout) {
        return Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(identifier))
                .andCommandKey(HystrixCommandKey.Factory.asKey(identifier))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerRequestVolumeThreshold(1)
                        .withCircuitBreakerErrorThresholdPercentage(1)
                        .withMetricsHealthSnapshotIntervalInMilliseconds(100)
                        .withExecutionTimeoutInMilliseconds(timeout));
    }
}
