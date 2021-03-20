package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.db.FailSafeDatabaseHandler;
import fj.data.Validation;
import org.glassfish.jersey.internal.util.Producer;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;

public interface AsyncRunner {

    /*
     * Asynchronous operations cannot be repeated as the result is only
     * committed once the request has finished. Then the result has already been
     * reported to the client and there is no point in repeating it.
     */
    default <O> Validation<StatusCode, O> runAsynchronously(AsyncResponse r, Producer<Validation<StatusCode, O>> operation) {
        r.register((CompletionCallback) this::finishTransaction);
        return operation.call();
    }

    default void finishTransaction(Throwable t) {
        if (t != null)
            getDbHandler().rollback();
        else
            getDbHandler().commit();
    }

    FailSafeDatabaseHandler getDbHandler();
}
