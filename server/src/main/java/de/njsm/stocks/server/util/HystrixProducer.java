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
                           FunctionWithExceptions<FunctionWithExceptions<I, O, E>, ProducerWithExceptions<O, E>, E> wrapper,
                           FunctionWithExceptions<I, O, E> client) {
        super(getHystrixConfig(resourceIdentifier));
        this.client = client;
        this.wrapper = wrapper;
        logState();
    }

    private void logState() {
        LOG.debug("health counts: " + getMetrics().getHealthCounts().toString());
        if (isCircuitBreakerOpen()) {
            LOG.warn("Circuit breaker open, returning immediately");
        } else {
            LOG.debug("Circuit breaker closed");
        }
    }

    @Override
    protected O run() throws E {
        return wrapper.apply(client).accept();
    }

    private static Setter getHystrixConfig(String identifier) {
        return Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(identifier))
                .andCommandKey(HystrixCommandKey.Factory.asKey(identifier))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerRequestVolumeThreshold(1)
                        .withCircuitBreakerErrorThresholdPercentage(1)
                        .withMetricsHealthSnapshotIntervalInMilliseconds(100));
    }
}
