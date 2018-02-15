package de.njsm.stocks.server.internal.db;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.internal.util.Producer;

public class HystrixFunction<O> extends HystrixCommand<O> {

    private static final Logger LOG = LogManager.getLogger(HystrixFunction.class);

    private Producer<O> producer;

    HystrixFunction(String resourceIdentifier,
                           Producer<O> producer) {
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
    protected O run() {
        return producer.call();
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
