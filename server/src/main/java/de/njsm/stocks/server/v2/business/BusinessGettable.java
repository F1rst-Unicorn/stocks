package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.Entity;
import de.njsm.stocks.server.v2.db.CrudDatabaseHandler;
import fj.data.Validation;
import org.jooq.TableRecord;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.stream.Stream;

public interface BusinessGettable<U extends TableRecord<U>, T extends Entity<T>> extends BusinessOperations {

    default Validation<StatusCode, Stream<T>> get(AsyncResponse r, boolean bitemporal, Instant startingFrom) {
        return runAsynchronously(r, () -> {
            getDbHandler().setReadOnly();
            return getDbHandler().get(bitemporal, startingFrom);
        });
    }

    CrudDatabaseHandler<U, T> getDbHandler();
}
