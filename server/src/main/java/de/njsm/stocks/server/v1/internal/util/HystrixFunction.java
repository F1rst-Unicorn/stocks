package de.njsm.stocks.server.v1.internal.util;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HystrixFunction<O, E extends Exception> extends HystrixCommand<O> {

    private static final Logger LOG = LogManager.getLogger(HystrixFunction.class);

    private ProducerWithExceptions<O, E> producer;

    public HystrixFunction(String resourceIdentifier,
                    ProducerWithExceptions<O, E> producer) {
        super(getHystrixConfig(resourceIdentifier));
        this.producer = producer;

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
        return producer.accept();
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
