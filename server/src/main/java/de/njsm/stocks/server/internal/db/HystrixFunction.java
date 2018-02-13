package de.njsm.stocks.server.internal.db;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.internal.util.Producer;

public class HystrixFunction<O> extends HystrixCommand<O> {

    private static final Logger LOG = LogManager.getLogger(HystrixConsumer.class);

    private Producer<O> producer;

    HystrixFunction(String resourceIdentifier,
                           Producer<O> producer) {
        super(HystrixCommandGroupKey.Factory.asKey(resourceIdentifier));
        this.producer = producer;

        logState();
    }

    private void logState() {
        if (isCircuitBreakerOpen()) {
            LOG.warn("Circuit breaker still open, returning immediately");
        } else {
            LOG.debug("Circuit breaker closed");
        }
    }

    @Override
    protected O run() {
        return producer.call();
    }
}
