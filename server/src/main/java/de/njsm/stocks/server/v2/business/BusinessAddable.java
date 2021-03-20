package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.Entity;
import de.njsm.stocks.server.v2.business.data.Insertable;
import de.njsm.stocks.server.v2.db.CrudDatabaseHandler;
import fj.data.Validation;
import org.jooq.TableRecord;

public interface BusinessAddable<U extends TableRecord<U>, T extends Entity<T>> extends BusinessOperations {

    default Validation<StatusCode, Integer> add(Insertable<U, T> item) {
        return runFunction(() -> getDbHandler().add(item));
    }

    CrudDatabaseHandler<U, T> getDbHandler();
}
