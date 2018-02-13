package de.njsm.stocks.server.internal.db;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class HystrixConsumer extends HystrixCommand<Void> {

    private static final Logger LOG = LogManager.getLogger(HystrixConsumer.class);

    private Consumer<Void> consumer;

    HystrixConsumer(String resourceIdentifier,
                           Consumer<Void> consumer) {
        super(HystrixCommandGroupKey.Factory.asKey(resourceIdentifier));
        this.consumer = consumer;

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
    protected Void run()  {
        consumer.accept(null);
        return null;
    }
}
