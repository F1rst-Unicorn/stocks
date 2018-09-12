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
            if (e.getCause() instanceof RuntimeException) {
                LOG.error("circuit breaker still open", e);
            } else {
                LOG.error("circuit breaker error", e);
            }
            return Validation.fail(getDefaultErrorCode());
        }
    }

    String getResourceIdentifier();

    StatusCode getDefaultErrorCode();

    <O> ProducerWithExceptions<Validation<StatusCode, O>, E>
    wrap(FunctionWithExceptions<I, Validation<StatusCode, O>, E> client);

}
